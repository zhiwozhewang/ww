/**
 * Created by YuGang Yang on August 14, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.lreader.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopeer.android.apps.lreader.ui.activities.BaseReaderActivity;
import com.loopeer.android.apps.lreader.utilities.TimeDateUtils;
import com.loopeer.android.apps.lreader.utilities.UiUtilities;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.R;
import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.util.UIUtil;
import org.geometerplus.android.util.ViewUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.BookmarkQuery;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.resources.ZLResource;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 书签
 */
public class BookmarksFragment extends Fragment {

    private static final int OPEN_ITEM_ID = 0;
    private static final int EDIT_ITEM_ID = 1;
    private static final int DELETE_ITEM_ID = 2;

    private final BookCollectionShadow myCollection = new BookCollectionShadow();
    private volatile Book myBook;

    private final Comparator<Bookmark> myComparator = new Bookmark.ByTimeComparator();

    private volatile BookmarksAdapter myThisBookAdapter;
//    private volatile BookmarksAdapter myAllBooksAdapter;
//    private volatile BookmarksAdapter mySearchResultsAdapter;

    private final ZLResource myResource = ZLResource.resource("bookmarksView");
//    private final ZLStringOption myBookmarkSearchPatternOption = new ZLStringOption("BookmarkSearch", "Pattern", "");

    private ListView mListView;
    private TextView mEmptyView;

    private FBReaderApp myFBReaderApp;


    public static BookmarksFragment createBookmarkFragment(Bundle arguments) {
        BookmarksFragment instance = new BookmarksFragment();
        instance.setArguments(arguments);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = BaseReaderActivity.fragmentArgumentsToIntent(getArguments());
        myBook = FBReaderIntents.getBookExtra(intent);

        myFBReaderApp = (FBReaderApp)FBReaderApp.Instance();
        if (myFBReaderApp == null) {
            myFBReaderApp = new FBReaderApp(new BookCollectionShadow());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.l_fragment_listview, container, false);
        mListView = UiUtilities.getView(view, android.R.id.list);
        mEmptyView = UiUtilities.getView(view, android.R.id.empty);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setEmptyView(mEmptyView);
    }

    private class Initializer implements Runnable {
        public void run() {
            if (myBook != null) {
                for (BookmarkQuery query = new BookmarkQuery(myBook, 20); ; query = query.next()) {
                    final List<Bookmark> thisBookBookmarks = myCollection.bookmarks(query);
                    if (thisBookBookmarks.isEmpty()) {
                        break;
                    }
                    getBookmarksAdapter().addAll(thisBookBookmarks);
//                    myAllBooksAdapter.addAll(thisBookBookmarks);
                }
            }
//            for (BookmarkQuery query = new BookmarkQuery(20); ; query = query.next()) {
//                final List<Bookmark> allBookmarks = myCollection.bookmarks(query);
//                if (allBookmarks.isEmpty()) {
//                    break;
//                }
//                myAllBooksAdapter.addAll(allBookmarks);
//            }
            if (getActivity() == null || getActivity().isFinishing()) return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getActivity().setProgressBarIndeterminateVisibility(false);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                getActivity().setProgressBarIndeterminateVisibility(true);
            }
        });

        myCollection.bindToService(getActivity(), new Runnable() {
            public void run() {
//                if (myAllBooksAdapter != null) {
//                    return;
//                }

                if (myBook != null) {
                    myThisBookAdapter = new BookmarksAdapter(mListView, false);
                }

                new Thread(new Initializer()).start();
            }
        });

//        Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
//        OrientationUtil.setOrientation(getActivity(), intent);
    }

    private BookmarksAdapter getBookmarksAdapter() {
        if (myThisBookAdapter == null) {
            myThisBookAdapter = new BookmarksAdapter(mListView, false);
        }

        return myThisBookAdapter;
    }

    @Override
    public void onDestroy() {
        myCollection.unbind();
        super.onDestroy();
    }


//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        final int position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
//        final ListView view = mListView;
//        final Bookmark bookmark = ((BookmarksAdapter)view.getAdapter()).getItem(position);
//        switch (item.getItemId()) {
//            case OPEN_ITEM_ID:
//                gotoBookmark(bookmark);
//                return true;
//            case EDIT_ITEM_ID:
//                final Intent intent = new Intent(getActivity(), BookmarkEditActivity.class);
//                OrientationUtil.startActivityForResult(getActivity(), intent, 1);
//                // TODO: implement
//                return true;
//            case DELETE_ITEM_ID:
//                myCollection.deleteBookmark(bookmark);
//                if (myThisBookAdapter != null) {
//                    myThisBookAdapter.remove(bookmark);
//                }
//                if (myAllBooksAdapter != null) {
//                    myAllBooksAdapter.remove(bookmark);
//                }
//                if (mySearchResultsAdapter != null) {
//                    mySearchResultsAdapter.remove(bookmark);
//                }
//                return true;
//        }
//        return super.onContextItemSelected(item);
//    }

    private void addBookmark() {
        Intent intent = BaseReaderActivity.fragmentArgumentsToIntent(getArguments());
        final Bookmark bookmark = FBReaderIntents.
                getBookmarkExtra(intent);
        if (bookmark != null) {
            myCollection.saveBookmark(bookmark);
            getBookmarksAdapter().add(bookmark);
//            myAllBooksAdapter.add(bookmark);
        }
    }

    private void gotoBookmark(Bookmark bookmark) {
        bookmark.markAsAccessed();
        myCollection.saveBookmark(bookmark);
        final Book book = myCollection.getBookById(bookmark.getBookId());
        if (book != null) {
            FBReader.openBookActivity(getActivity(), book, bookmark);
        } else {
            UIUtil.showErrorMessage(getActivity(), "cannotOpenBook");
        }
    }

    private final class BookmarksAdapter extends BaseAdapter implements AdapterView.OnItemClickListener/*, View.OnCreateContextMenuListener*/ {
        private final List<Bookmark> myBookmarks =
                Collections.synchronizedList(new LinkedList<Bookmark>());
        private final boolean myShowAddBookmarkItem;

        BookmarksAdapter(ListView listView, boolean showAddBookmarkItem) {
            myShowAddBookmarkItem = showAddBookmarkItem;
            listView.setAdapter(this);
            listView.setOnItemClickListener(this);
//            listView.setOnCreateContextMenuListener(this);
        }

        public List<Bookmark> bookmarks() {
            return Collections.unmodifiableList(myBookmarks);
        }

        public void addAll(final List<Bookmark> bookmarks) {
            if (getActivity() == null || getActivity().isFinishing()) return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    synchronized (myBookmarks) {
                        for (Bookmark b : bookmarks) {
                            final int position = Collections.binarySearch(myBookmarks, b, myComparator);
                            if (position < 0) {
                                myBookmarks.add(-position - 1, b);
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void add(final Bookmark b) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    synchronized (myBookmarks) {
                        final int position = Collections.binarySearch(myBookmarks, b, myComparator);
                        if (position < 0) {
                            myBookmarks.add(-position - 1, b);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void remove(final Bookmark b) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    myBookmarks.remove(b);
                    notifyDataSetChanged();
                }
            });
        }

        public void clear() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    myBookmarks.clear();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public final boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public final boolean isEnabled(int position) {
            return true;
        }


        @Override
        public int getCount() {
            return myShowAddBookmarkItem ? myBookmarks.size() + 1 : myBookmarks.size();
        }

        @Override
        public final Bookmark getItem(int position) {
            if (myShowAddBookmarkItem) {
                --position;
            }
            return (position >= 0) ? myBookmarks.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = (convertView != null) ? convertView :
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.l_bookmark_item, parent, false);
            final TextView textView = ViewUtil.findTextView(view, R.id.bookmark_item_text);
            final TextView bookTitleView = ViewUtil.findTextView(view, R.id.bookmark_item_booktitle);
            final TextView bookCreatedView = ViewUtil.findTextView(view, R.id.bookmark_item_created_at);

            final Bookmark bookmark = getItem(position);
            TOCTree tocTree = myFBReaderApp.getTOCElement(bookmark);
            if (tocTree != null) {
                bookTitleView.setText(tocTree.getText());
            } else {
                bookTitleView.setText(bookmark.getBookTitle());
            }
            textView.setText(bookmark.getText());
            bookCreatedView.setText(TimeDateUtils.getFormatTime(bookmark.getDate(Bookmark.DateType.Creation).getTime() / 1000));
            return view;
        }

//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            final int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
//            if (getItem(position) != null) {
//                menu.setHeaderTitle(getItem(position).getText());
//                menu.add(0, OPEN_ITEM_ID, 0, myResource.getResource("open").getValue());
//                //menu.add(0, EDIT_ITEM_ID, 0, myResource.getResource("edit").getValue());
//                menu.add(0, DELETE_ITEM_ID, 0, myResource.getResource("delete").getValue());
//            }
//        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Bookmark bookmark = getItem(position);
            if (bookmark != null) {
                gotoBookmark(bookmark);
            } else {
                addBookmark();
            }
        }
    }

}
