/**
 * Created by YuGang Yang on August 14, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.lreader.ui.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopeer.android.apps.lreader.ui.adapter.ZLTreeAdapter;
import com.loopeer.android.apps.lreader.utilities.UiUtilities;

import org.geometerplus.android.fbreader.R;
import org.geometerplus.android.util.ViewUtil;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.tree.ZLTree;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;

/**
 * 目录 Table Of Contents
 */
public class TOCFragment extends Fragment {

    private static final int PROCESS_TREE_ITEM_ID = 0;
    private static final int READ_BOOK_ITEM_ID = 1;

    private ListView mListView;

    private TOCAdapter myAdapter;
    private ZLTree<?> mySelectedItem;

    public static TOCFragment createCatalogFragment() {
        return new TOCFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.l_fragment_listview, container, false);
        mListView = UiUtilities.getView(view, android.R.id.list);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FBReaderApp fbreader = (FBReaderApp)ZLApplication.Instance();
        final TOCTree root = fbreader.Model.TOCTree;
        myAdapter = new TOCAdapter(root);
        final ZLTextWordCursor cursor = fbreader.BookTextView.getStartCursor();
        int index = cursor.getParagraphIndex();
        if (cursor.isEndOfParagraph()) {
            ++index;
        }
        TOCTree treeToSelect = fbreader.getCurrentTOCElement();
        myAdapter.selectItem(treeToSelect);
        mySelectedItem = treeToSelect;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
        final TOCTree tree = (TOCTree)myAdapter.getItem(position);
        switch (item.getItemId()) {
            case PROCESS_TREE_ITEM_ID:
                myAdapter.runTreeItem(tree);
                return true;
            case READ_BOOK_ITEM_ID:
                myAdapter.openBookText(tree);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private final class TOCAdapter extends ZLTreeAdapter {
        TOCAdapter(TOCTree root) {
            super(mListView, root);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            final int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
            final TOCTree tree = (TOCTree)getItem(position);
            if (tree.hasChildren()) {
                menu.setHeaderTitle(tree.getText());
                final ZLResource resource = ZLResource.resource("tocView");
                menu.add(0, PROCESS_TREE_ITEM_ID, 0, resource.getResource(isOpen(tree) ? "collapseTree" : "expandTree").getValue());
                menu.add(0, READ_BOOK_ITEM_ID, 0, resource.getResource("readText").getValue());
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = (convertView != null) ? convertView :
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.toc_tree_item, parent, false);
            final TOCTree tree = (TOCTree)getItem(position);
//            view.setBackgroundColor(tree == mySelectedItem ? 0xff808080 : 0);
            setIcon(ViewUtil.findImageView(view, R.id.toc_tree_item_icon), tree);
            ViewUtil.findTextView(view, R.id.toc_tree_item_text).setText(tree.getText());
            return view;
        }

        void openBookText(TOCTree tree) {
            final TOCTree.Reference reference = tree.getReference();
            if (reference != null) {
                getActivity().finish();
                final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
                fbreader.addInvisibleBookmark();
                fbreader.BookTextView.gotoPosition(reference.ParagraphIndex, 0, 0);
                fbreader.showBookTextView();
                fbreader.storePosition();
            }
        }

        @Override
        protected boolean runTreeItem(ZLTree<?> tree) {
            if (super.runTreeItem(tree)) {
                return true;
            }
            openBookText((TOCTree)tree);
            return true;
        }
    }

}
