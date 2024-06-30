package com.carrydream.cardrecorder.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carrydream.cardrecorder.BaseApplication;
import com.carrydream.cardrecorder.Model.GameType;
import com.carrydream.cardrecorder.Model.Item;
import com.carrydream.cardrecorder.Model.Platform;
import com.carrydream.cardrecorder.Model.ZdyPoker;
import com.carrydream.cardrecorder.adapter.AddAdapter;
import com.carrydream.cardrecorder.adapter.NumAdapter;
import com.carrydream.cardrecorder.adapter.ZdyAdapter;
import com.carrydream.cardrecorder.base.BaseFragment;
import com.carrydream.cardrecorder.base.BaseMessage;
import com.carrydream.cardrecorder.base.BaseResult;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.tool.DensityUtils;
import com.carrydream.cardrecorder.tool.EventUtil;
import com.carrydream.cardrecorder.tool.Game;
import com.carrydream.cardrecorder.tool.MyToast;
import com.carrydream.cardrecorder.tool.Tool;
import com.carrydream.cardrecorder.tool.ZdyCount;
import com.carrydream.cardrecorder.ui.Module.HomeModule;
import com.carrydream.cardrecorder.ui.Presenter.HomePresenter;
import com.carrydream.cardrecorder.ui.activity.HtmlActivity;
import com.carrydream.cardrecorder.ui.activity.LoginActivity;
//import com.carrydream.cardrecorder.ui.component.DaggerHomeComponent;
import com.carrydream.cardrecorder.ui.component.DaggerHomeComponent;
import com.carrydream.cardrecorder.ui.contacts.HomeContacts;
import com.carrydream.cardrecorder.ui.dialog.AddPlatformDialog;
import com.carrydream.cardrecorder.ui.dialog.DeleteDialog;
import com.carrydream.cardrecorder.ui.dialog.SelectGameDialog;
import com.carrydream.cardrecorder.ui.dialog.SelectHandNumberDialog;
import com.carrydream.cardrecorder.ui.dialog.SelectNumberDialog;
import com.carrydream.cardrecorder.ui.dialog.SelectPlatformDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemChildLongClickListener;
//import com.google.android.gms.common.internal.ImagesContract;
import com.hb.aiyouxiba.R;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/* loaded from: classes.dex */
public class HomeFragment extends BaseFragment implements HomeContacts.View {
    @BindView(R.id.aPair)
    RelativeLayout aPair;
    @BindView(R.id.add)
    ImageView add;
    AddAdapter addAdapter;
    AddPlatformDialog addPlatformDialog;
    @BindView(R.id.add_layout)
    LinearLayout add_layout;
    @BindView(R.id.amount)
    TextView amount;
    @BindView(R.id.game_name)
    TextView game_name;
    GridLayoutManager gridLayoutManager1;
    GridLayoutManager gridLayoutManager2;
    GridLayoutManager gridLayoutManager3;
    GridLayoutManager gridLayoutManager4;
    @BindView(R.id.hand_amount)
    TextView hand_amount;
    @BindView(R.id.home)
    TextView home;
    @BindView(R.id.name)
    TextView name;
    NumAdapter numAdapter;
    NumAdapter numAdapter2;
    List<Platform> platformList;
    @Inject
    HomePresenter presenter;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.recycler1)
    RecyclerView recycler2;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.recycler_zdy)
    RecyclerView recycler_zdy;
    SelectPlatformDialog selectPlatformDialog;
    ZdyAdapter zdyAdapter;
    @BindView(R.id.zdy_lay)
    LinearLayout zdy_lay;

    @Override // com.carrydream.cardrecorder.base.BaseView
    public /* synthetic */ LifecycleTransformer<HomeContacts.Presenter> bindUntilEvent(FragmentEvent fragmentEvent) {
        return CC.$default$bindUntilEvent(this, fragmentEvent);
    }

    @Override // com.carrydream.cardrecorder.base.BaseFragment
    protected void lazyLoad() {
    }

    @Override // com.carrydream.cardrecorder.base.BaseFragment
    protected int setLayoutId() {
        return R.layout.home_fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override // com.carrydream.cardrecorder.base.BaseFragment
    protected void init() {
        DaggerHomeComponent.builder().appComponent(BaseApplication.getAppComponent()).homeModule(new HomeModule(this)).build().inject(this);
        setRecyclerData();
        this.presenter.support();
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setRecyclerData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        this.gridLayoutManager1 = gridLayoutManager;
        this.recycler.setLayoutManager(gridLayoutManager);
        NumAdapter numAdapter = new NumAdapter(getActivity(), R.layout.num_item);
        this.numAdapter = numAdapter;
        numAdapter.addChildClickViewIds(R.id.item);
        this.numAdapter.setOnItemChildClickListener(new OnItemChildClickListener() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda2
            @Override // com.chad.library.adapter.base.listener.OnItemChildClickListener
            public final void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                HomeFragment.lambda$setRecyclerData$0(baseQuickAdapter, view, i);
            }
        });
        this.recycler.setAdapter(this.numAdapter);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new Item("3人", false));
        arrayList.add(new Item("4人", false));
        arrayList.add(new Item("5人", false));
        arrayList.add(new Item("6人", false));
        this.numAdapter.setNewInstance(arrayList);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getContext(), 4);
        this.gridLayoutManager2 = gridLayoutManager2;
        this.recycler2.setLayoutManager(gridLayoutManager2);
        NumAdapter numAdapter2 = new NumAdapter(getActivity(), R.layout.num_item);
        this.numAdapter2 = numAdapter2;
        numAdapter2.addChildClickViewIds(R.id.item);
        this.numAdapter2.setOnItemChildClickListener(new OnItemChildClickListener() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda12
            @Override // com.chad.library.adapter.base.listener.OnItemChildClickListener
            public final void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                HomeFragment.this.m137xb26a64c2(baseQuickAdapter, view, i);
            }
        });
        this.recycler2.setAdapter(this.numAdapter2);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new Item("斗地主", true));
        arrayList2.add(new Item("跑的快16", true));
        arrayList2.add(new Item("跑的快15", true));
        arrayList2.add(new Item("自定义", true));
        this.numAdapter2.setNewInstance(arrayList2);
        this.numAdapter2.setSelect(DataUtils.getInstance().getItem());
        GridLayoutManager gridLayoutManager3 = new GridLayoutManager(getContext(), 14);
        this.gridLayoutManager3 = gridLayoutManager3;
        this.recycler_zdy.setLayoutManager(gridLayoutManager3);
        ZdyAdapter zdyAdapter = new ZdyAdapter(getActivity(), R.layout.zdy_item);
        this.zdyAdapter = zdyAdapter;
        zdyAdapter.addChildClickViewIds(R.id.item);
        this.zdyAdapter.setOnItemChildClickListener(new OnItemChildClickListener() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda1
            @Override // com.chad.library.adapter.base.listener.OnItemChildClickListener
            public final void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                HomeFragment.this.m139xe6a16200(baseQuickAdapter, view, i);
            }
        });
        this.recycler_zdy.setAdapter(this.zdyAdapter);
        if (DataUtils.getInstance().getItem() == 3) {
            this.zdy_lay.setVisibility(0);
            TextView textView = this.amount;
            textView.setText(DataUtils.getInstance().getNumber() + "副");
            TextView textView2 = this.hand_amount;
            textView2.setText(DataUtils.getInstance().getHand() + "张");
            this.zdyAdapter.setNewInstance(Tool.initDataZdy(DataUtils.getInstance().getPoker()));
        } else {
            this.zdyAdapter.setNewInstance(ZdyCount.getCount(DataUtils.getInstance().getNumber()));
        }
        GridLayoutManager gridLayoutManager4 = new GridLayoutManager(getContext(), 3);
        this.gridLayoutManager4 = gridLayoutManager4;
        this.recyclerView.setLayoutManager(gridLayoutManager4);
        AddAdapter addAdapter = new AddAdapter(getActivity(), R.layout.num_item);
        this.addAdapter = addAdapter;
        addAdapter.addChildLongClickViewIds(R.id.item);
        this.addAdapter.setOnItemChildLongClickListener(new OnItemChildLongClickListener() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda3
            @Override // com.chad.library.adapter.base.listener.OnItemChildLongClickListener
            public final boolean onItemChildLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                return HomeFragment.this.m141x1ad85f3e(baseQuickAdapter, view, i);
            }
        });
        this.recyclerView.setAdapter(this.addAdapter);
        if (DataUtils.getInstance().getPlatform() != null && DataUtils.getInstance().getPlatform().size() > 0) {
            this.add_layout.setVisibility(0);
            this.addAdapter.setNewInstance(DataUtils.getInstance().getPlatform());
        } else {
            this.add_layout.setVisibility(8);
        }
        setItemDecoration();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$setRecyclerData$0(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        if (view.getId() != R.id.item) {
            return;
        }
        MyToast.show("该功能正在研发中，敬请期待！");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$setRecyclerData$1$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    @SuppressLint("WrongConstant")
    public /* synthetic */ void m137xb26a64c2(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        HashMap<String, Integer> initDataZdy;
        int num;
        Item item = (Item) baseQuickAdapter.getItem(i);
        if (view.getId() != R.id.item) {
            return;
        }
        this.numAdapter2.setSelect(i);
        new HashMap();
        if (item.getName().equals("斗地主")) {
            initDataZdy = Tool.initData();
            num = 17;
            this.zdy_lay.setVisibility(8);
        } else if (item.getName().equals("跑的快16")) {
            initDataZdy = Tool.initData16();
            num = 16;
            this.zdy_lay.setVisibility(8);
        } else if (item.getName().equals("跑的快15")) {
            initDataZdy = Tool.initData15();
            num = 15;
            this.zdy_lay.setVisibility(8);
        } else {
            initDataZdy = Tool.initDataZdy(this.zdyAdapter.getData());
            num = getNum(initDataZdy) / 3;
            this.zdy_lay.setVisibility(0);
            TextView textView = this.amount;
            textView.setText(DataUtils.getInstance().getNumber() + "副");
        }
        DataUtils.getInstance().setPoker(initDataZdy);
        DataUtils.getInstance().setItem(i);
        DataUtils.getInstance().setHand(num);
        TextView textView2 = this.hand_amount;
        textView2.setText(DataUtils.getInstance().getHand() + "张");
        EventUtil.post(new BaseMessage(false, 0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$setRecyclerData$3$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ void m139xe6a16200(BaseQuickAdapter baseQuickAdapter, View view, final int i) {
        final ZdyPoker zdyPoker = (ZdyPoker) baseQuickAdapter.getItem(i);
        if (view.getId() != R.id.item) {
            return;
        }
        SelectNumberDialog selectNumberDialog = new SelectNumberDialog(getActivity(), zdyPoker.getSum() + 1, "选择" + zdyPoker.getName() + "的数量", false);
        selectNumberDialog.setNumberSelect(new SelectNumberDialog.NumberSelect() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda10
            @Override // com.carrydream.cardrecorder.ui.dialog.SelectNumberDialog.NumberSelect
            public final void Select(int i2) {
                HomeFragment.this.m138xcc85e361(zdyPoker, i, i2);
            }
        });
        selectNumberDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$setRecyclerData$2$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ void m138xcc85e361(ZdyPoker zdyPoker, int i, int i2) {
        zdyPoker.setCount(i2);
        this.zdyAdapter.setData(i, zdyPoker);
        HashMap<String, Integer> initDataZdy = Tool.initDataZdy(this.zdyAdapter.getData());
        DataUtils.getInstance().setPoker(initDataZdy);
        DataUtils.getInstance().setHand(getNum(initDataZdy) / 3);
        TextView textView = this.hand_amount;
        textView.setText(DataUtils.getInstance().getHand() + "张");
        EventUtil.post(new BaseMessage(false, 0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$setRecyclerData$5$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ boolean m141x1ad85f3e(BaseQuickAdapter baseQuickAdapter, View view, final int i) {
        Item item = (Item) baseQuickAdapter.getItem(i);
        if (view.getId() != R.id.item) {
            return false;
        }
        DeleteDialog deleteDialog = new DeleteDialog(getActivity(), item);
        deleteDialog.setNewListener(new DeleteDialog.NewSubmit() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda6
            @Override // com.carrydream.cardrecorder.ui.dialog.DeleteDialog.NewSubmit
            public final void delete(Item item2) {
                HomeFragment.this.m140xbce09f(i, item2);
            }
        });
        deleteDialog.show();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$setRecyclerData$4$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    @SuppressLint("WrongConstant")
    public /* synthetic */ void m140xbce09f(int i, Item item) {
        List<Item> platform = DataUtils.getInstance().getPlatform();
        platform.remove(i);
        DataUtils.getInstance().setPlatform(platform);
        if (platform.size() > 0) {
            this.addAdapter.setNewInstance(platform);
        } else {
            this.add_layout.setVisibility(8);
        }
    }

    public int getNum(HashMap<String, Integer> hashMap) {
        int i = 0;
        for (String str : hashMap.keySet()) {
            i += hashMap.get(str).intValue();
        }
        return i;
    }

    @OnClick({R.id.game_type, R.id.aPair, R.id.help, R.id.hand_amount_layout, R.id.add, R.id.platform})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.aPair /* 2131230740 */:
                SelectNumberDialog selectNumberDialog = new SelectNumberDialog(getActivity(), 6, "选择牌的副数", true);
                selectNumberDialog.setNumberSelect(new SelectNumberDialog.NumberSelect() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda9
                    @Override // com.carrydream.cardrecorder.ui.dialog.SelectNumberDialog.NumberSelect
                    public final void Select(int i) {
                        HomeFragment.this.m134xdedd0c64(i);
                    }
                });
                selectNumberDialog.show();
                return;
            case R.id.add /* 2131230801 */:
//                if (DataUtils.getInstance().getUser() == null) {
//                    startActivity(new Intent(getContext(), LoginActivity.class));
//                    return;
//                } else

                    if (this.addPlatformDialog == null) {
                    AddPlatformDialog addPlatformDialog = new AddPlatformDialog(getActivity());
                    this.addPlatformDialog = addPlatformDialog;
                    addPlatformDialog.setEditListener(new AddPlatformDialog.Edit() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda5
                        @Override // com.carrydream.cardrecorder.ui.dialog.AddPlatformDialog.Edit
                        public final void add(String str) {
                            HomeFragment.this.m136x131409a2(str);
                        }
                    });
                    this.addPlatformDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnDismissListener
                        public final void onDismiss(DialogInterface dialogInterface) {
                            HomeFragment.this.m130x281baf00(dialogInterface);
                        }
                    });
                    this.addPlatformDialog.show();
                    return;
                }
            case R.id.game_type /* 2131230970 */:
                SelectGameDialog selectGameDialog = new SelectGameDialog(getActivity(), Game.getAllGame());
                selectGameDialog.setGameSelect(new SelectGameDialog.GameSelect() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda7
                    @Override // com.carrydream.cardrecorder.ui.dialog.SelectGameDialog.GameSelect
                    public final void Select(GameType gameType) {
                        HomeFragment.this.m133xc4c18dc5(gameType);
                    }
                });
                selectGameDialog.show();
                return;
            case R.id.hand_amount_layout /* 2131230984 */:
                SelectHandNumberDialog selectHandNumberDialog = new SelectHandNumberDialog(getActivity(), getNum(DataUtils.getInstance().getPoker()) / 3, "选择手牌的数量");
                selectHandNumberDialog.setNumberSelect(new SelectHandNumberDialog.NumberSelect() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda8
                    @Override // com.carrydream.cardrecorder.ui.dialog.SelectHandNumberDialog.NumberSelect
                    public final void Select(int i) {
                        HomeFragment.this.m135xf8f88b03(i);
                    }
                });
                selectHandNumberDialog.show();
                return;
            case R.id.help /* 2131230986 */:
                Intent intent = new Intent(getContext(), HtmlActivity.class);
                intent.putExtra("title", "帮助");
                intent.putExtra("url", DataUtils.getInstance().getConfig().getHelp_h5());
                startActivity(intent);
                return;
            case R.id.platform /* 2131231193 */:
                if (this.selectPlatformDialog != null || this.platformList == null) {
                    return;
                }
                SelectPlatformDialog selectPlatformDialog = new SelectPlatformDialog(getActivity(), this.platformList);
                this.selectPlatformDialog = selectPlatformDialog;
                selectPlatformDialog.setPlatforSelect(new SelectPlatformDialog.PlatformSelect() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda11
                    @Override // com.carrydream.cardrecorder.ui.dialog.SelectPlatformDialog.PlatformSelect
                    public final void Select(Platform platform) {
                        HomeFragment.this.m131x42372d9f(platform);
                    }
                });
                this.selectPlatformDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment$$ExternalSyntheticLambda4
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        HomeFragment.this.m132x5c52ac3e(dialogInterface);
                    }
                });
                this.selectPlatformDialog.show();
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewClicked$6$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ void m133xc4c18dc5(GameType gameType) {
        this.game_name.setText(gameType.getName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewClicked$7$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ void m134xdedd0c64(int i) {
        this.zdyAdapter.setNewInstance(ZdyCount.getCount(i));
        DataUtils.getInstance().setNumber(i);
        TextView textView = this.amount;
        textView.setText(i + "副");
        DataUtils.getInstance().setPoker(Tool.initDataZdy(this.zdyAdapter.getData()));
        DataUtils.getInstance().setHand(getNum(DataUtils.getInstance().getPoker()) / 3);
        TextView textView2 = this.hand_amount;
        textView2.setText(DataUtils.getInstance().getHand() + "张");
        EventUtil.post(new BaseMessage(false, 0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewClicked$8$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ void m135xf8f88b03(int i) {
        DataUtils.getInstance().setHand(i);
        TextView textView = this.hand_amount;
        textView.setText(i + "张");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewClicked$9$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ void m136x131409a2(String str) {
        if (DataUtils.getInstance().getPlatform() != null && DataUtils.getInstance().getPlatform().size() >= 3) {
            MyToast.show("自定义游戏名已达到上限，请删除已添加的游戏");
        } else {
            this.presenter.report(0, str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewClicked$10$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ void m130x281baf00(DialogInterface dialogInterface) {
        this.addPlatformDialog = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewClicked$11$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ void m131x42372d9f(Platform platform) {
        this.presenter.report(platform.getId(), platform.getPlatform());
        DataUtils.getInstance().setGamePlatform(platform);
        this.name.setText(DataUtils.getInstance().getGamePlatform().getPlatform());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewClicked$12$com-carrydream-cardrecorder-ui-fragment-HomeFragment  reason: not valid java name */
    public /* synthetic */ void m132x5c52ac3e(DialogInterface dialogInterface) {
        this.selectPlatformDialog = null;
    }

    public void setItemDecoration() {
        this.recycler.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment.1
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = layoutParams.getSpanSize();
                layoutParams.getSpanIndex();
                if (spanSize != HomeFragment.this.gridLayoutManager1.getSpanCount()) {
                    if (spanSize == 0) {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 0.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 5.0f);
                    } else if (spanSize == 3) {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 5.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 0.0f);
                    } else {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 5.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 5.0f);
                    }
                }
            }
        });
        this.recycler2.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment.2
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = layoutParams.getSpanSize();
                layoutParams.getSpanIndex();
                if (spanSize != HomeFragment.this.gridLayoutManager2.getSpanCount()) {
                    if (spanSize == 0) {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 0.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 5.0f);
                    } else if (spanSize == 3) {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 5.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 0.0f);
                    } else {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 5.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 5.0f);
                    }
                }
            }
        });
        this.recycler_zdy.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment.3
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = layoutParams.getSpanSize();
                layoutParams.getSpanIndex();
                if (spanSize != HomeFragment.this.gridLayoutManager3.getSpanCount()) {
                    if (spanSize == 0) {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 4.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 2.0f);
                    } else if (spanSize == 13) {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 2.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 4.0f);
                    } else {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 2.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 2.0f);
                    }
                }
            }
        });
        this.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: com.carrydream.cardrecorder.ui.fragment.HomeFragment.4
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = layoutParams.getSpanSize();
                layoutParams.getSpanIndex();
                if (spanSize != HomeFragment.this.gridLayoutManager4.getSpanCount()) {
                    if (spanSize == 0) {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 4.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 2.0f);
                    } else if (spanSize == 2) {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 2.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 4.0f);
                    } else {
                        rect.left = DensityUtils.dp2px(HomeFragment.this.getContext(), 4.0f);
                        rect.right = DensityUtils.dp2px(HomeFragment.this.getContext(), 4.0f);
                    }
                }
            }
        });
    }

    @Override // com.carrydream.cardrecorder.ui.contacts.HomeContacts.View
    public void OnSupport(BaseResult<List<Platform>> baseResult) {
        if (baseResult.getStatus() == 200) {
            this.platformList = baseResult.getData();
            if (DataUtils.getInstance().getGamePlatform() == null) {
                DataUtils.getInstance().setGamePlatform(baseResult.getData().get(0));
            }
            this.name.setText(DataUtils.getInstance().getGamePlatform().getPlatform());
            return;
        }
        MyToast.show(baseResult.getMessage());
    }

    @SuppressLint("WrongConstant")
    @Override // com.carrydream.cardrecorder.ui.contacts.HomeContacts.View
    public void OnReport(BaseResult<Object> baseResult, int i, String str) {
        if (baseResult.getStatus() != 200) {
            MyToast.show(baseResult.getMessage());
        } else if (i == 0) {
            List<Item> platform = DataUtils.getInstance().getPlatform() != null ? DataUtils.getInstance().getPlatform() : new ArrayList<>();
            platform.add(new Item(str));
            DataUtils.getInstance().setPlatform(platform);
            this.add_layout.setVisibility(0);
            this.addAdapter.setNewInstance(platform);
        }
    }
}
