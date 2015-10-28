package com.longyuan.qm;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.longyuan.qm.activity.HomeActivity;

import java.util.LinkedList;

public class ActivityManager {
    private static LinkedList<Activity> activityStack;
    private static ActivityManager instance;

    private ActivityManager() {
    }

    public static ActivityManager getScreenManager() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 退出栈顶Activity
     *
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            Log.i("Activity管理", activity.getClass().getSimpleName() + "弹出栈");
            activity.finish();
            activity = null;
        }
    }

    /**
     * 获得当前栈顶Activity
     *
     * @return
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (activityStack != null && !activityStack.isEmpty())
            activity = activityStack.get(activityStack.size() - 1);
        return activity;
    }

    /**
     * 将当前Activity推入栈中
     *
     * @param activity
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new LinkedList<Activity>();
        }
        activityStack.add(activity);
        Log.i("Activity管理", activity.getClass().getSimpleName() + "加入栈中");
    }

    /**
     * 退出栈中所有Activity
     */
    public void popAllActivityExceptOne() {
        while (true) {
            Activity activity = currentActivity();
            if (null == activity) {
                break;
            }
            // if (activity != null) {
            // activity.finish();
            // }
            popActivity(activity);
        }
    }

    public void popAllActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (null == activity) {
                break;
            }
            // if (activity != null) {
            // activity.finish();
            // }
            popActivity(activity);
        }
    }

    /**
     * 退出指定名字的activity
     */
    public void popPointNameActivity(String name) {
        while (true) {
            Activity activity = currentActivity();
            if (null == activity) {
                break;
            }

            String activityName = activity.getComponentName().getClassName();
            if (TextUtils.equals(name, activityName)) {
                continue;
            }

            // if (activity != null) {
            // activity.finish();
            // }
            popActivity(activity);
        }
    }

    /**
     * 获得当前ACTIVITY 名字
     */
    public String getCurrentActivityName() {
        Activity activity = currentActivity();
        String name = "";
        if (activity != null) {
            name = activity.getComponentName().getClassName().toString();
        }
        return name;
    }

    // 返回大厅页面
    public void gotoHomeActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (activity != null) {
                Log.i("Activity:", activity.getClass().getSimpleName() + "预报弹出栈");
                if (activity instanceof HomeActivity) {
                    break;
                }
                // if (activity != null) {
                // activity.finish();
                // }
                popActivity(activity);
            }
        }
    }

//	public boolean isSearchActivity() {
//		for (int j = 0; j < activityStack.size(); j++) {
//			Activity activity = activityStack.get(j);
//			if (activity instanceof SearchActivity) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	// 返回大厅页面
//	public void popToSearchActivity() {
//		while (true) {
//			Activity activity = currentActivity();
//			LOG.i("Activity:", activity.getClass().getSimpleName() + "预报弹出栈");
//			if (activity instanceof SearchActivity) {
//				break;
//			}
//			// if (activity != null) {
//			// activity.finish();
//			// }
//			popActivity(activity);
//		}
//	}

//	public boolean isCatalogActivity() {
//		for (int j = 0; j < activityStack.size(); j++) {
//			Activity activity = activityStack.get(j);
//			if (activity instanceof CatalogActivity) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	// 返回大厅页面
//	public void popToCatalogActivity() {
//		while (true) {
//			Activity activity = currentActivity();
//			LOG.i("Activity:", activity.getClass().getSimpleName() + "预报弹出栈");
//			if (activity instanceof CatalogActivity) {
//				break;
//			}
//			popActivity(activity);
//		}
//	}

}
