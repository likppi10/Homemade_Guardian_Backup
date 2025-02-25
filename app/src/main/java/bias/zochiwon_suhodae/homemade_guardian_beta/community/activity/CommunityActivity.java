package bias.zochiwon_suhodae.homemade_guardian_beta.community.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import bias.zochiwon_suhodae.homemade_guardian_beta.Main.activity.HostModelActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta.Main.common.FirebaseHelper;
import bias.zochiwon_suhodae.homemade_guardian_beta.Main.common.SendNotification;
import bias.zochiwon_suhodae.homemade_guardian_beta.R;
import bias.zochiwon_suhodae.homemade_guardian_beta.Main.common.FirestoreAdapter;
import bias.zochiwon_suhodae.homemade_guardian_beta.Main.activity.BasicActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta.Main.common.BackPressEditText;
import bias.zochiwon_suhodae.homemade_guardian_beta.Main.common.listener.OnPostListener;
import bias.zochiwon_suhodae.homemade_guardian_beta.community.adapter.CommunityViewPagerAdapter;
import bias.zochiwon_suhodae.homemade_guardian_beta.model.community.CommunityModel;
import bias.zochiwon_suhodae.homemade_guardian_beta.model.community.Community_CommentModel;
import bias.zochiwon_suhodae.homemade_guardian_beta.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta.model.market.Market_CommentModel;
import bias.zochiwon_suhodae.homemade_guardian_beta.model.user.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;

// 게시물을 클릭하여 들어온 게시물의 상세정보에 대한 액티비티이다.
// 게시물의 제목, 내용, 작성자, 작성자 이미지, 게시물에 추가한 이미지 등이 있고, 하단부에 채팅과 댓글을 달 수 있는 기능이 있다.
// Ex) 커뮤니티 프레그먼트에서 게시물을 클릭하였을 때 모두 이 액티비티가 발생한다.

public class CommunityActivity extends BasicActivity {              // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
                                                                    // 2. 변수 및 배열
    private CommunityModel Communitymodel;                              // CommunityModel 선언
    private UserModel Usermodel;                                        // UserModel 선언
    private String currentUser_Uid;                                          // 현재 사용자의 Uid
    private String Current_NickName = null;                             // 현재 사용자의 닉네임
    private String Comment_Host_Image;                                  // 댓글 작성자의 이미지
    private ArrayList<String> ImageList = new ArrayList<>();            // 게시물의 이미지 리스트
                                                                    // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private ConstraintLayout ViewPagerLayout;                           // 뷰페이져가 존재하는 layout 영역
    private ViewPager Viewpager;                                        // ImageList를 보여주기 위한 ViewPager
    private ImageView Host_UserPage_ImageView;                          // 게시물 작성자의 프로필 ImageView
    private ImageButton Like_ImageButton;                               // 좋아요 Button
    private Button Comment_Write_Button;                                // 댓글 작성 Button
    private TextView Title_TextView;                                    // 게시물의 제목 TextView
    private TextView TextContents_TextView;                             // 게시물의 내용 TextView
    private TextView DateOfManufacture_TextView;                        // 게시물의 작성일 TextView
    private TextView Like_TextView;                                     // 게시물의 좋아요 개수 TextView
    private BackPressEditText Comment_Input_EditText;                   // 댓글 내용 EditText
                                                                    // 4. 파이어베이스 관련 선언
    private FirebaseUser CurrentUser;                                   // 현재 사용자
    private FirebaseHelper Firebasehelper;                              // FirebaseHelper 선언
    private FirestoreAdapter Comment_Firestoreadapter;                  // FirestoreAdapter 선언

    @Override
    public void onStart() {
        super.onStart();
        if (Comment_Firestoreadapter != null) {
            Comment_Firestoreadapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Comment_Firestoreadapter != null) {
            Comment_Firestoreadapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

       // 레이아웃 find, setOnClickListener
        LinearLayout Scrollview;
        Scrollview = (LinearLayout) findViewById(R.id.Scrollbar);
        Scrollview.setOnClickListener(onClickListener);

       // 게시물의 작성일, 제목, 내용, 좋아요 개수 find
        DateOfManufacture_TextView = findViewById(R.id.Post_DateOfManufacture);
        Title_TextView = findViewById(R.id.Post_Title);
        TextContents_TextView = findViewById(R.id.Post_TextContents);
        Like_TextView = findViewById(R.id.Like_TextView);

       // 작성자 프로필, 좋아요, 댓글 작성, 댓글 작성 버튼 find
        Host_UserPage_ImageView = (ImageView) findViewById(R.id.Host_UserPage_ImageButton);
        Like_ImageButton = (ImageButton) findViewById(R.id.Like_ImageButton);
        Comment_Write_Button = findViewById(R.id.Comment_Write_Button);
        Comment_Input_EditText = (BackPressEditText) findViewById(R.id.Comment_Input_EditText);

       // 채팅하기, 작성자 프로필, 좋아요, 댓글 작성, 댓글 작성 버튼 setOnClickListener
        Host_UserPage_ImageView.setOnClickListener(onClickListener);
        Like_ImageButton.setOnClickListener(onClickListener);
        Comment_Write_Button.setOnClickListener(onClickListener);
        Comment_Input_EditText.setOnClickListener(onClickListener);

       // 메뉴 버튼 활성화
        findViewById(R.id.menu).setOnClickListener(onClickListener);

       // Communitymodel을 getIntent()
        Communitymodel = (CommunityModel) getIntent().getSerializableExtra("communityInfo");

       // 현재 사용자 설정
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser_Uid = CurrentUser.getUid();

       // Firebasehelper을 setting
        Firebasehelper = new FirebaseHelper(this);
        Firebasehelper.setOnpostlistener(onPostListener);

       // 현재 유저의 정보를 받는 함수
        Get_CurrentUser_Info();

       // 게시물 정보, 게시물 이미지 유무 설정, 좋아요 버튼 활성화 설정, 채팅 버튼 활성화 설정, 작성자 정보 설정 함수
        Setting_Community();

       //댓글 목록
        Comment_Firestoreadapter = new RecyclerViewAdapter(FirebaseFirestore.getInstance().collection("COMMUNITY").
                document(Communitymodel.getCommunityModel_Community_Uid()).collection("COMMENT").orderBy("community_CommentModel_DateOfManufacture"));
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommunityActivity.this));
        recyclerView.setAdapter(Comment_Firestoreadapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    Communitymodel = (CommunityModel) data.getSerializableExtra("communityInfo");
                    Title_TextView.setText(Communitymodel.getCommunityModel_Title());
                    TextContents_TextView.setText(Communitymodel.getCommunityModel_Text());
                    ImageList = Communitymodel.getCommunityModel_ImageList();
                    if(ImageList != null) {
                        Viewpager = findViewById(R.id.ViewPager);
                        Viewpager.setAdapter(new CommunityViewPagerAdapter(this, ImageList, "Enable","Community"));
                        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
                        indicator.setViewPager(Viewpager);
                    }else{
                        ViewPagerLayout = (ConstraintLayout) findViewById(R.id.ViewPagerLayout);
                        ViewPagerLayout.setVisibility(View.GONE);
                    }
                    onResume();
                }
                break;
        }
    }

   // 댓글을 쓰는 사람 (현재 이용자) 의 정보를 미리 받는다.
    public void Get_CurrentUser_Info(){
        DocumentReference docRefe_USERS_CurrentUid = FirebaseFirestore.getInstance().collection("USERS").document(currentUser_Uid);
        docRefe_USERS_CurrentUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Usermodel = documentSnapshot.toObject(UserModel.class);
                Current_NickName = Usermodel.getUserModel_NickName();
                Comment_Host_Image = Usermodel.getUserModel_ProfileImage();
            }
        });
    }

   // 게시물 정보, 게시물 이미지 유무 설정, 좋아요 버튼 활성화 설정, 작성자 정보 설정
    public void Setting_Community(){

       // 게시물 작성일, 제목, 내용 setText 설정
        DateOfManufacture_TextView.setText(new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(Communitymodel.getCommunityModel_DateOfManufacture()));
        Title_TextView.setText(Communitymodel.getCommunityModel_Title());
        TextContents_TextView.setText(Communitymodel.getCommunityModel_Text());

       // 게시물의 이미지가 있다면 ViewPager 설정
        ImageList = Communitymodel.getCommunityModel_ImageList();
        if(ImageList != null) {
            Viewpager = findViewById(R.id.ViewPager);
            Viewpager.setAdapter(new CommunityViewPagerAdapter(this, ImageList, "Enable", "Community"));
            CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(Viewpager);
        }else{
            ViewPagerLayout = (ConstraintLayout) findViewById(R.id.ViewPagerLayout);
            ViewPagerLayout.setVisibility(View.GONE);
        }

       // 좋아요 버튼의 활성화 상태 결정
        Like_TextView.setText(String.valueOf(Communitymodel.getCommunityModel_LikeList().size()));
        int Check_Like = 0;
        for(int count = 0; count < Communitymodel.getCommunityModel_LikeList().size() ; count ++){
            if(currentUser_Uid.equals(Communitymodel.getCommunityModel_LikeList().get(count))){
                Glide.with(getApplicationContext()).load(R.drawable.heart).into(Like_ImageButton);
                Check_Like = 1;
            }
        }
        if(Check_Like == 0) {
            Glide.with(getApplicationContext()).load(R.drawable.empty_heart).into(Like_ImageButton);
        }

       // 작성자의 닉네임, 프로필 성정
        DocumentReference docRef_USERS_HostUid = FirebaseFirestore.getInstance().collection("USERS").document(Communitymodel.getCommunityModel_Host_Uid());
        docRef_USERS_HostUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Usermodel = documentSnapshot.toObject(UserModel.class);
                TextView Community_Host_Name_TextView;
                Community_Host_Name_TextView = findViewById(R.id.Post_Host_Name);
                if(Usermodel.getUserModel_ProfileImage() != null){
                    Glide.with(CommunityActivity.this).load(Usermodel.getUserModel_ProfileImage()).centerInside().override(500).into(Host_UserPage_ImageView);
                    Community_Host_Name_TextView.setText(Usermodel.getUserModel_NickName());
                }
                else{
                    Glide.with(getApplicationContext()).load(R.drawable.none_profile_user).centerCrop().override(500).into(Host_UserPage_ImageView);
                    Community_Host_Name_TextView.setText(Usermodel.getUserModel_NickName());
                }
            }
        });
    }

   // 작성자의 프로필 이미지, 채팅 버튼, 댓글 작성 버튼, 댓글 작성 텍스트, 댓글 작성하는 곳의 상단 영역, 좋아요, 메뉴의 ClickListener
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

               // 작성자의 프로필 이미지 : 클릭시 HostModelActivity로 이동한다.
                case R.id.Host_UserPage_ImageButton:
                    Intent Intent_HostModelActivity = new Intent(getApplicationContext(), HostModelActivity.class);
                    Intent_HostModelActivity.putExtra("toUid", Communitymodel.getCommunityModel_Host_Uid());
                    startActivity(Intent_HostModelActivity);
                    break;

               // 댓글 작성 버튼 : 댓글 작성 텍스트에서 받아온 값으로 댓글을 작성한다.
                case R.id.Comment_Write_Button:
                    String Comment = CommunityActivity.this.Comment_Input_EditText.getText().toString();
                    if(Comment.equals("")){
                        Toast.makeText(getApplicationContext(), "댓글을 입력하십시오.", Toast.LENGTH_SHORT).show();
                    }else{
                        Write_Comment(Comment, Current_NickName, Comment_Host_Image);
                        CommunityActivity.this.Comment_Input_EditText.setText("");
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        if(!Usermodel.getUserModel_Uid().equals(currentUser_Uid)){
                            SendAlarm(Comment);
                        }
                    }
                    break;

               // 댓글 작성 텍스트 : 댓글을 작성 받는 EditText
                case R.id.Comment_Input_EditText:
                    Comment_Input_EditText.setFocusableInTouchMode(true);
                    Comment_Input_EditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    break;

               // 댓글 작성 곳의 상단 영역 : 댓글 입력 도중에 키보드 윗 부분을 클릭하면 키보드를 숨긴다.
                case R.id.Scrollbar:
                    InputMethodManager immHide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    immHide.hideSoftInputFromWindow(Comment_Input_EditText.getWindowToken(), 0);
                    break;

               // 좋아요 버튼
                case R.id.Like_ImageButton:
                    int Check_Like = 0;
                   // for문  : 좋아요를 누른적이 있다면 Check_Like = 1 / 좋아요를 누른적이 없다면 Check_Like = 0
                    for (int count = 0; count < Communitymodel.getCommunityModel_LikeList().size(); count++) {
                        if (currentUser_Uid.equals(Communitymodel.getCommunityModel_LikeList().get(count))) {
                           Check_Like++;
                        }
                    }
                   // if문 : 좋아요를 누른적이 없다면
                    if (Check_Like == 0) {
                        Glide.with(getApplicationContext()).load(R.drawable.heart).into(Like_ImageButton);
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        ArrayList<String> LikeList = new ArrayList<String>();
                        final DocumentReference documentReference = firebaseFirestore.collection("COMMUNITY").document(Communitymodel.getCommunityModel_Community_Uid());
                        LikeList = Communitymodel.getCommunityModel_LikeList();

                       // LikeList에 본인 Uid를 추가
                        LikeList.add(currentUser_Uid);

                       // 핫게시물의 상태를 설정
                        if (LikeList.size() >= 10) {
                            Communitymodel.setCommunityModel_HotCommunity("O");
                        }
                        if (LikeList.size() < 10) {
                            Communitymodel.setCommunityModel_HotCommunity("X");
                        }
                        Communitymodel.setCommunityModel_LikeList(LikeList);
                        documentReference.set(Communitymodel.getCommunityInfo())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                        Like_TextView.setText(String.valueOf(Communitymodel.getCommunityModel_LikeList().size()));
                    }
                   // if문 : 좋아요를 눌렀다면
                    if(Check_Like == 1){
                        Glide.with(getApplicationContext()).load(R.drawable.empty_heart).into(Like_ImageButton);
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        ArrayList<String> LikeList = new ArrayList<String>();
                        final DocumentReference documentReference = firebaseFirestore.collection("COMMUNITY").document(Communitymodel.getCommunityModel_Community_Uid());
                        LikeList = Communitymodel.getCommunityModel_LikeList();
                       // LikeList에 본인 Uid를 제거
                        LikeList.remove(currentUser_Uid);
                       // 핫게시물의 상태를 설정
                        if (LikeList.size() > 0) {
                            Communitymodel.setCommunityModel_HotCommunity("O");
                        }
                        if (LikeList.size() <= 0) {
                            Communitymodel.setCommunityModel_HotCommunity("X");
                        }
                        Communitymodel.setCommunityModel_LikeList(LikeList);
                        documentReference.set(Communitymodel.getCommunityInfo())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                        Like_TextView.setText(String.valueOf(Communitymodel.getCommunityModel_LikeList().size()));
                    }
                    break;

               // 메뉴 버튼
                case R.id.menu:
                    showPopup(v);
                    break;
            }
        }
    };
    private void SendAlarm(final String MessageModel_Message) {
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(currentUser_Uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {  //데이터의 존재여부
                            final UserModel userModel = document.toObject(UserModel.class);
                            if(currentUser_Uid != Communitymodel.getCommunityModel_Host_Uid()){
                                final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(Communitymodel.getCommunityModel_Host_Uid());
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) {
                                                if (document.exists()) {  //데이터의 존재여부
                                                    UserModel TouserModel = document.toObject(UserModel.class);
                                                    SendNotification.sendCommentNotification(TouserModel.getUserModel_Token(), userModel.getUserModel_NickName(), "자유게시판의 댓글이 달렸습니다!", Communitymodel.getCommunityModel_Community_Uid());
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });

    }

   // 작성자 정보 우측에 있는 점 3개의 메뉴 버튼
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(CommunityActivity.this, v);

       // 작성자의 메뉴와 작성자가 아닌 유저의 메뉴가 다르다.
        if(CurrentUser.getUid().equals(Communitymodel.getCommunityModel_Host_Uid())){
            getMenuInflater().inflate(R.menu.post_host, popup.getMenu());
        }
        else{
            getMenuInflater().inflate(R.menu.community_comment_guest, popup.getMenu());
        }
       // 메뉴에서의 MenuItem이 클릭 되었을 때의 Listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                   // 유저가 작성자일 때
                    case R.id.Post_Delete_Button:
                        Firebasehelper.Community_Storagedelete(Communitymodel,"delete");
                        finish();
                        return true;
                    case R.id.Post_Modify_Button:
                        myStartActivity(ModifyCommunityActivity.class, Communitymodel);
                        return true;

                   // 유저가 작성자가 아닐 때
                    case R.id.Comment_Report_Button:
                        Toast.makeText(getApplicationContext(), "신고 되었습니다.", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

   // 댓글을 작성하는 함수
    private void Write_Comment(final String Comment, final String Host_Name, final String Comment_Host_Image) {

       // 댓글이 등록되어 지는 동안에는 댓글 작성 버튼을 비활성화 시킨다.
        Comment_Write_Button.setEnabled(false);

       // 댓글을 넣을 COMMENT의 Uid를 미리 생성
        String Comment_Uid = null;
        Comment_Uid = FirebaseFirestore.getInstance().collection("COMMUNITY").document(Communitymodel.getCommunityModel_Community_Uid()).collection("COMMENT").document().getId();

        Date DateOfManufacture = new Date();
        final Community_CommentModel Community_Commentmodel;
        Community_Commentmodel = new Community_CommentModel(currentUser_Uid, Comment,  DateOfManufacture, Host_Name, Comment_Uid, Communitymodel.getCommunityModel_Community_Uid(),Comment_Host_Image);

        final DocumentReference docRef_COMMUNITY_CommunityUid = FirebaseFirestore.getInstance().collection("COMMUNITY").document(Communitymodel.getCommunityModel_Community_Uid());
        final String CommentID = Comment_Uid;
        docRef_COMMUNITY_CommunityUid.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                WriteBatch Batch_COMMENT_CommentUid = FirebaseFirestore.getInstance().batch();
               // 생성해 놓은 Uid에 댓글을 set
                Batch_COMMENT_CommentUid.set(docRef_COMMUNITY_CommunityUid.collection("COMMENT").document(CommentID), Community_Commentmodel);
                Batch_COMMENT_CommentUid.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //sendGCM();
                           // 댓글이 작성된 것이 확인 되면 다시 댓글 작성버튼을 활성화 시킨다.
                            Comment_Write_Button.setEnabled(true);

                           // 게시물의 댓글의 개수+1
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            int commentcount;
                            final DocumentReference documentReference = firebaseFirestore.collection("COMMUNITY").document(Communitymodel.getCommunityModel_Community_Uid());
                            commentcount = Integer.parseInt(String.valueOf(Communitymodel.getCommunityModel_CommentCount()));
                            commentcount++;
                            Communitymodel.setCommunityModel_CommentCount(commentcount);
                            documentReference.set(Communitymodel.getCommunityInfo())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    }
                });
            }

        });
    }

   //댓글을 화면에 생성해주는 RecyclerView
    class RecyclerViewAdapter extends FirestoreAdapter<CustomViewHolder> {
        final private RequestOptions Requestoptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));
        private StorageReference Storagereference;

        RecyclerViewAdapter(Query query) {
            super(query);
            Storagereference = FirebaseStorage.getInstance().getReference();
            //setUnread2Read();
        }


        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false));
        }

        @Override
        public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
            DocumentSnapshot DocumentSnapshot = getSnapshot(position);
            final Community_CommentModel community_commentModel = DocumentSnapshot.toObject(Community_CommentModel.class);
            viewHolder.Comment_UserName_TextView.setText(community_commentModel.getCommunity_CommentModel_Host_Name());
            viewHolder.Comment_UserComment_TextView.setText(community_commentModel.getCommunity_CommentModel_Comment());
            viewHolder.Comment_DateOfManufacture.setText(
                    new SimpleDateFormat("MM/dd hh:mm",
                            Locale.getDefault()).format(community_commentModel.getCommunity_CommentModel_DateOfManufacture()));

            if (community_commentModel.getCommunity_CommentModel_Host_Image()!=null) {
                Glide.with(CommunityActivity.this).load(community_commentModel.getCommunity_CommentModel_Host_Image()).centerInside().override(500).into(viewHolder.Comment_UserProfile_ImageView);
            } else{
                Glide.with(CommunityActivity.this).load(R.drawable.none_profile_user).centerCrop().override(500).into(viewHolder.Comment_UserProfile_ImageView);
            }

            viewHolder.Comment_Menu_CardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu Menu_Popup = new PopupMenu(CommunityActivity.this, view);
                    Menu_Popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {

                               // 댓글 작성자라면 삭제하기 버튼만 있음
                                case R.id.Comment_Delete_Button:
                                    Firebasehelper.Community_Comment_Storedelete(community_commentModel, Communitymodel);
                                    return true;

                               // 댓글 작성자가 아니면 신고하기 버튼만 있음
                                case R.id.Comment_Report_Button:
                                    Toast.makeText(getApplicationContext(), "신고 되었습니다.", Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    MenuInflater Menu_Inflater = Menu_Popup.getMenuInflater();
                    if(CurrentUser.getUid().equals(community_commentModel.getCommunity_CommentModel_Host_Uid())){
                        Menu_Inflater.inflate(R.menu.comment_host, Menu_Popup.getMenu());
                    }
                    else{
                        Menu_Inflater.inflate(R.menu.community_comment_guest, Menu_Popup.getMenu());
                    }
                    Menu_Popup.show();}
                }
            );

        }
    }

   // 댓글의 Data
    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView Comment_UserProfile_ImageView;
        public TextView Comment_UserName_TextView;
        public TextView Comment_UserComment_TextView;
        public CardView Comment_Menu_CardView;
        public TextView Comment_DateOfManufacture;

        CustomViewHolder(View view) {
            super(view);
            Comment_UserProfile_ImageView = view.findViewById(R.id.Comment_UserProfile);
            Comment_UserName_TextView = view.findViewById(R.id.Comment_UserName);
            Comment_UserComment_TextView = view.findViewById(R.id.Comment_UserComment);
            Comment_Menu_CardView = view.findViewById(R.id.Comment_Menu);
            Comment_DateOfManufacture = view.findViewById(R.id.Comment_DateOfManufacture);

        }
    }

   // 파이어베이스 헬퍼의 listener
    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(MarketModel marketModel) { }
        @Override
        public void oncommentDelete(Market_CommentModel market_commentModel) { }
        @Override
        public void oncommunityDelete(CommunityModel communityModel) { Log.e("로그 ","삭제 성공"); }
        @Override
        public void oncommnitycommentDelete(Community_CommentModel community_commentModel) { Log.e("로그 ","댓글 삭제 성공"); }
        @Override
        public void onModify() { Log.e("로그 ","수정 성공"); }
    };

    private void myStartActivity(Class c, CommunityModel communityModel) {
        Intent Intent_Community_Data = new Intent(this, c);
        Intent_Community_Data.putExtra("communityInfo", communityModel);
        startActivityForResult(Intent_Community_Data, 0);
        finish();
    }

    @Override public void onResume() { super.onResume(); }
    private void didBackPressOnEditText() { finish(); }
}