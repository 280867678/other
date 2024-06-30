package com.example.floatdragview;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class DownloadCenterActivityFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener{

    /* renamed from: a */
    static final String f17935a = "DownloadCenterActivityFragment";

    /* renamed from: y */
    private static boolean f17936y = false;

    /* renamed from: C */
    private boolean f17939C;

    /* renamed from: D */
    private boolean f17940D;

    /* renamed from: F */
    private int f17942F;

    /* renamed from: b */
    FrameLayout f17946b;

    /* renamed from: c */
    boolean f17947c;

    /* renamed from: e */
    private FrameLayout f17949e;

    /* renamed from: f */
    private FrameLayout f17950f;

    /* renamed from: g */
    private RelativeLayout f17951g;

    /* renamed from: h */
//    private DownloadCenterMenuPopWindow f17952h;

    /* renamed from: i */
//    private DownloadCreateMoreTaskDialog f17953i;

    /* renamed from: j */
    private AppBarLayout f17954j;

    /* renamed from: k */
    private CoordinatorLayout f17955k;

    /* renamed from: l */
    private TabLayout f17956l;

    /* renamed from: m */
    private DownloadCenterViewPager f17957m;

    /* renamed from: n */
    private C4778c f17958n;

    /* renamed from: p */
    private FrameLayout f17960p;

    /* renamed from: s */
    private FrameLayout f17963s;

    /* renamed from: u */
    private FrameLayout f17965u;

    /* renamed from: v */
    private FrameLayout f17966v;

    /* renamed from: w */
    private CoordinatorLayout.Behavior f17967w;

    /* renamed from: z */
//    private CooperationItem f17969z;

    /* renamed from: o */
//    private final C4777b f17959o = new C4777b(this, (byte) 0);

    /* renamed from: q */
    private boolean f17961q = false;

    /* renamed from: r */
    private boolean f17962r = false;

    /* renamed from: t */
    private Handler f17964t = new Handler();

    /* renamed from: x */
    private int f17968x = 0;

    /* renamed from: A */
    private final int f17937A = 1000;

    /* renamed from: B */
//    private Runnable f17938B = new RunnableC4790b(this);

    /* renamed from: d */
//    final DownloadCenterControl f17948d = new DownloadCenterControl();

    /* renamed from: E */
    private boolean f17941E = false;

    /* renamed from: G */
//    private C4776a f17943G = new C4776a(this, (byte) 0);

    /* renamed from: H */
//    private PrivateSpaceMgr.InterfaceC5121a f17944H = new C4811w(this);

    /* renamed from: I */
//    private LoginCompletedObservers f17945I = new C4857x(this);

    /* renamed from: c */
    static /* synthetic */ int m10813c() {
        return 4;
    }

    public class C4778c extends FragmentPagerAdapter {

        /* renamed from: a */
        protected TaskListPageFragment f17973a;

        /* renamed from: b */
        protected TaskListPageFragment f17974b;

        /* renamed from: c */
        protected TaskListPageFragment f17975c;

        /* renamed from: d */
        protected CollectionAndHistoryFragment f17976d;

        /* renamed from: e */
        protected final int f17977e;

        /* renamed from: f */
        protected long f17978f;

        /* renamed from: g */
        protected boolean f17979g;

        /* renamed from: i */
        private boolean f17981i;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C4778c(FragmentManager fragmentManager) {
            super(fragmentManager);

            this.f17977e = DownloadCenterActivityFragment.m10813c();
            this.f17979g = false;
        }

        /* renamed from: a */
        public final void m10779a(long j, boolean z) {
            this.f17978f = j;
            this.f17979g = z;
            if (this.f17973a != null) {
//                this.f17973a.m9054a(this.f17978f, this.f17979g);
                this.f17978f = -1L;
                this.f17979g = false;
            }
        }

        /* renamed from: a */
//        public final DownloadCenterTabBaseFragment m10781a() {
//            return getItem(DownloadCenterActivityFragment.this.f17957m.getCurrentItem());
//        }

        /* renamed from: b */
//        public final List<TaskCardItem> m10777b() {
//            ArrayList arrayList = new ArrayList();
//            if (DownloadCenterActivityFragment.this.f17957m.getCurrentItem() < 3) {
//                arrayList.addAll(((TaskListPageFragment) getItem(DownloadCenterActivityFragment.this.f17957m.getCurrentItem())).f20274d.m9005f());
//            }
//            return arrayList;
//        }

        /* renamed from: c */
        public final boolean m10776c() {
            if (DownloadCenterActivityFragment.this.f17957m.getCurrentItem() < 3) {
                return ((TaskListPageFragment) getItem(DownloadCenterActivityFragment.this.f17957m.getCurrentItem())).m9040g();
            }
            return false;
        }

        /* renamed from: a */
        public final void m10778a(boolean z) {
            if (this.f17981i != z) {
                this.f17981i = z;
                if (this.f17973a != null) {
                    this.f17973a.m9050a(z);
                }
                if (this.f17974b != null) {
                    this.f17974b.m9050a(z);
                }
                if (this.f17975c != null) {
                    this.f17975c.m9050a(z);
                }
            }
        }

        @Override // android.support.p003v4.app.FragmentPagerAdapter
        /* renamed from: a */
        public final DownloadCenterTabBaseFragment getItem(int i) {
            if (i == 0) {
                if (this.f17973a == null) {
                    this.f17973a = TaskListPageFragment.m9055a(0);
//                    this.f17973a.f20275e = DownloadCenterActivityFragment.this;
//                    this.f17973a.m9053a(DownloadCenterActivityFragment.this.f17948d);
//                    this.f17973a.m9054a(this.f17978f, this.f17979g);
                    this.f17973a.m9050a(this.f17981i);
                }
                return this.f17973a;
            } else if (i == 1) {
                if (this.f17974b == null) {
                    this.f17974b = TaskListPageFragment.m9055a(1);
//                    this.f17974b.f20275e = DownloadCenterActivityFragment.this;
//                    this.f17974b.m9053a(DownloadCenterActivityFragment.this.f17948d);
                    this.f17974b.m9050a(this.f17981i);
                }
                return this.f17974b;
            } else if (i != 2) {
                if (i == 3) {
                    if (this.f17976d == null) {
                        this.f17976d = CollectionAndHistoryFragment.m1739a(1002);
                    }
                    return this.f17976d;
                }
                return null;
            } else {
                if (this.f17975c == null) {
                    this.f17975c = TaskListPageFragment.m9055a(2);
//                    this.f17975c.f20275e = DownloadCenterActivityFragment.this;
//                    this.f17975c.m9053a(DownloadCenterActivityFragment.this.f17948d);
//                    this.f17975c.m9050a(this.f17981i);
                }
                return this.f17975c;
            }
        }

        @Override // android.support.p003v4.view.PagerAdapter
        public final int getCount() {
            return this.f17977e;
        }

        @Override // android.support.p003v4.view.PagerAdapter
        public final CharSequence getPageTitle(int i) {
            if (i == 0) {
                return DownloadCenterActivityFragment.this.getString(R.string.download_list_title_all);
            }
            if (i == 1) {
                return DownloadCenterActivityFragment.this.getString(R.string.download_list_title_unfinished);
            }
            if (i == 2) {
                return DownloadCenterActivityFragment.this.getString(R.string.download_list_title_finished);
            }
            if (i == 3) {
                return DownloadCenterActivityFragment.this.getString(R.string.download_list_title_collection);
            }
            return super.getPageTitle(i);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_download_center_activity, container, false);
        this.f17946b =  inflate.findViewById(R.id.download_center_title);
//        this.f17946b.setLeftImageViewClickListener(new View$OnClickListenerC4783ad(this));
//        this.f17946b.setRightImageView1ClickListener(new View$OnClickListenerC4784ae(this));
//        this.f17946b.setRightImageView2ClickListener(new View$OnClickListenerC4785af(this));
//        this.f17946b.setRightImageView3ClickListner(new View$OnClickListenerC4791c(this));
//        this.f17946b.setRightNavSearchClickListener(new View$OnClickListenerC4792d(this));
//        this.f17946b.setRightNavSearchShow(false);
//        this.f17946b.setTouchListener(new View$OnClickListenerC4793e(this));
        this.f17949e =  inflate.findViewById(R.id.download_center_select_file_title);
//        this.f17949e.setCancelListener(new View$OnClickListenerC4794f(this));
//        this.f17949e.setSelectAllListener(new C4795g(this));
        this.f17950f =  inflate.findViewById(R.id.bottom_operate_view);
//        this.f17950f.setDeleteTasksListener(new View$OnClickListenerC4796h(this));
//        this.f17950f.setPauseTasksListener(new View$OnClickListenerC4799k(this));
//        this.f17950f.setStartTasksListener(new View$OnClickListenerC4800l(this));
        this.f17960p =  inflate.findViewById(R.id.storageView);
        this.f17951g =  inflate.findViewById(R.id.cloud_sync_bottom_card);
//        CollectionCloudSyncHelper.m1802a().f30901a = this.f17951g;
        this.f17963s =  inflate.findViewById(R.id.downloadBriefInfo);
//        this.f17963s.setActionListener(new C4781ab(this));
//        DownloadBriefInfoHeaderView downloadBriefInfoHeaderView = this.f17963s;
//        LoginHelper.m6932a();
//        boolean m6780b = UserLoginImpl.m6780b();
//        boolean m6872l = LoginHelper.m6932a().m6872l();
//        downloadBriefInfoHeaderView.getStatusInfo().f18059a = m6780b;
//        downloadBriefInfoHeaderView.getStatusInfo().f18060b = m6872l;
        this.f17956l =  inflate.findViewById(R.id.tabLayout);
        this.f17957m = (DownloadCenterViewPager) inflate.findViewById(R.id.taskListViewPager);
        this.f17957m.setOffscreenPageLimit(4);
        this.f17957m.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.search_line_space_normal));
        this.f17958n = new C4778c(getFragmentManager());
        this.f17957m.setAdapter(this.f17958n);
        this.f17956l.setupWithViewPager(this.f17957m);
//        GotoTopBtnHelper.m8708a().m8707a(inflate.findViewById(R.id.iv_go_to_top));
        if (getActivity() != null && (getActivity() instanceof DownloadCenterActivity) && !((DownloadCenterActivity) getActivity()).f17926d) {
//            m10808d(this.f17957m.getCurrentItem());
        }
        this.f17957m.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {
//                DownloadCenterActivityFragment.C4778c c4778c;
//                DownloadCenterActivityFragment.C4778c c4778c2;
//                DownloadStorageView downloadStorageView;
//                DownloadCenterActivityFragment.m10812c(i);
//                TaskListManager.m8543e().m8564a(i);
//                this.f18207a.m10808d(i);
//                CollectionCloudSyncHelper m1802a = CollectionCloudSyncHelper.m1802a();
//                m1802a.f30902b = false;
//                m1802a.m1801b();
//                this.f18207a.f17940D = false;
//                if (i == 3) {
//                    this.f18207a.f17940D = true;
//                    downloadStorageView = this.f18207a.f17960p;
//                    downloadStorageView.setVisibility(8);
//                    DownloadCenterActivityFragment.m10807d(this.f18207a);
//                    DownloadCenterActivityFragment.m10815b(this.f18207a, true);
//                    GotoTopBtnHelper.m8708a().m8705a(false);
//                } else {
//                    GotoTopBtnHelper.m8708a().m8705a(true);
//                    DownloadCenterActivityFragment.m10815b(this.f18207a, false);
//                }
//                c4778c = this.f18207a.f17958n;
//                c4778c.m10781a().mo1740a();
//                if (i == 2 || i == 1) {
//                    c4778c2 = this.f18207a.f17958n;
//                    if (DownloadCenterActivityFragment.this.f17957m.getCurrentItem() < 3) {
//                        TaskListPageFragment taskListPageFragment = (TaskListPageFragment) c4778c2.m10781a();
//                        if (taskListPageFragment.f20273c != null) {
//                            taskListPageFragment.f20273c.stopNestedScroll();
//                        }
//                    }
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        this.f17954j = (AppBarLayout) inflate.findViewById(R.id.appbar);
        this.f17954j.addOnOffsetChangedListener(this);
//        m10803e(getArguments().getBoolean("extra_key_jump_to_collection", false));
        this.f17955k = (CoordinatorLayout) inflate.findViewById(R.id.main_content);
        this.f17966v = (FrameLayout) inflate.findViewById(R.id.tabLayout_appbar_container);
        this.f17965u = (FrameLayout) inflate.findViewById(R.id.tabLayout_title_container);

        return inflate;
    }

    @SuppressLint("WrongConstant")
    @Override // android.support.design.widget.AppBarLayout.OnOffsetChangedListener
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        this.f17968x = i;
        if (this.f17947c) {
            return;
        }
        if (Math.abs(i) > this.f17963s.getHeight() - 18) {
            m10814b(true);
            if (this.f17942F != i) {
//                m10810c(false);
                this.f17942F = i;
            }
            if (!this.f17947c && this.f17958n.m10776c() ) {
                this.f17960p.setVisibility(8);
            }
//            DownloadADReportHelper.m12156a().m12155a(true);
        } else {
//            DownloadADReportHelper.m12156a().m12155a(false);
            m10814b(false);
            if (!this.f17940D) {
                this.f17960p.setVisibility(0);
            }
        }
//        m10809d();
    }


    private void m10814b(boolean z) {
        if (z) {
            this.f17941E = true;
//            if (this.f17946b.m10738a(0, true)) {
//                m10810c(false);
//                return;
//            }
            return;
        }
//        if (this.f17946b.m10738a(4, true)) {
//            m10810c(false);
//        }
        this.f17941E = false;
    }



    public final void m10827a() {
        this.f17947c = false;
//        m10819a(true);
//        this.f17956l.setTabLayoutEnable(true);
        this.f17957m.setCanScroll(true);
        this.f17958n.m10778a(false);
//        this.f17949e.m10744c(true);
//        this.f17950f.m10753a(true);
//        GotoTopBtnHelper.m8708a().m8705a(true);
    }



}