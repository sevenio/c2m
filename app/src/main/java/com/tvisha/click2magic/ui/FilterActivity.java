package com.tvisha.click2magic.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.adapter.FilterAgentsAdapter;
import com.tvisha.click2magic.adapter.FilterSitesAdapter;
import com.tvisha.click2magic.adapter.SearchAgentsAdapter;
import com.tvisha.click2magic.adapter.SearchPropertyAdapter;
import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.api.C2mApiInterface;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.AgentInfo;
import com.tvisha.click2magic.api.post.model.AgentsResponse;
import com.tvisha.click2magic.constants.ApiEndPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindAnim;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.actionBack)
    ImageView actionBack;

    @BindView(R.id.actionClose)
    ImageView actionClose;

    @BindView(R.id.actionLable)
    TextView actionLable;

    @BindView(R.id.actionClear)
    TextView actionClear;

    @BindView(R.id.from_date_tv)
    TextView from_date_tv;

    @BindView(R.id.to_date_tv)
    TextView to_date_tv;

    @BindView(R.id.agentsLayout)
    RelativeLayout agentsLayout;

    @BindView(R.id.propertyLayout)
    RelativeLayout propertyLayout;

    @BindView(R.id.agents_recyclerView)
    RecyclerView agents_recyclerView;

    @BindView(R.id.selected_agents_recyclerView)
    RecyclerView selected_agents_recyclerView;

    @BindView(R.id.property_recyclerView)
    RecyclerView property_recyclerView;

    @BindView(R.id.selected_property_recyclerView)
    RecyclerView selected_property_recyclerView;

    @BindView(R.id.agent_search_et)
    EditText agent_search_et;

    @BindView(R.id.property_search_et)
    EditText property_search_et;

    @BindView(R.id.actionAgentSelect)
    Button actionAgentSelect;

    @BindView(R.id.actionSiteSelect)
    Button actionSiteSelect;

    @BindView(R.id.actionApply)
    Button actionApply;

    @BindView(R.id.agentsSearchLayout)
    LinearLayout agentsSearchLayout;

    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    @BindView(R.id.propertySearchLayout)
    LinearLayout propertySearchLayout;

    @BindView(R.id.dateLayout)
    LinearLayout dateLayout;

    @BindView(R.id.fromDateLayout)
    LinearLayout fromDateLayout;

    @BindView(R.id.toDateLayout)
    LinearLayout toDateLayout;


    @BindView(R.id.line)
    View line;

    LinearLayoutManager layoutManager = null, layoutManager1, layoutManager2, layoutManager4;

    SearchAgentsAdapter agentsAdapter = null;
    FilterAgentsAdapter filterAgentsAdapter = null;
    List<AgentInfo> agentsList = new ArrayList<>();
    List<AgentInfo> selectedAgentList = new ArrayList<>();
    List<AgentInfo> selectedTempAgentList = new ArrayList<>();
    SearchPropertyAdapter propertyAdapter = null;
    FilterSitesAdapter filterSitesAdapter = null;
    List<SitesInfo> siteList = new ArrayList<>();
    List<SitesInfo> selectedSiteList = new ArrayList<>();
    List<SitesInfo> selectedTempSiteList = new ArrayList<>();
    String dateFormat1 = "yyyy-MM-dd", dateFormat = "dd MMM yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat1, Locale.US);

    String frmDt = "", toDt = "", filter_agent_ids = "", filter_site_ids = "";
    C2mApiInterface apiService;
    Dialog dialog = null;
    String user_id = "", company_token = "", role = "";
    long timeInMills = 0;
    boolean isSelectedAgents=false,isSelectedSites=false;

    Activity activity;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        activity=FilterActivity.this;
        context=FilterActivity.this;

        progressDialog();
        getSharedPreferenceData();
        apiService = ApiClient.getClient().create(C2mApiInterface.class);
        handleIntent();
        initViews();
        initListenrs();

    }

    private void handleIntent() {
        try {
            siteList =Session.getSiteInfoList(FilterActivity.this, Session.SP_SITE_INFO);

            if (siteList != null && siteList.size() > 0) {
                for (int i = 0; i < siteList.size(); i++) {
                    siteList.get(i).setChecked(false);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        /*Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //agentsList =bundle.getParcelableArrayList(Values.IntentData.ACTIVE_AGENT_LIST);

        }*/
    }

    private void initListenrs() {
        try {
            actionBack.setOnClickListener(this);
            agentsLayout.setOnClickListener(this);
            dateLayout.setOnClickListener(this);
            fromDateLayout.setOnClickListener(this);
            toDateLayout.setOnClickListener(this);
            propertyLayout.setOnClickListener(this);
            actionClose.setOnClickListener(this);
            actionClear.setOnClickListener(this);
            actionApply.setOnClickListener(this);
            actionAgentSelect.setOnClickListener(this);
            actionSiteSelect.setOnClickListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initViews() {

        try {


            actionLable.setText("Filters");


            layoutManager1 = new LinearLayoutManager(FilterActivity.this, LinearLayoutManager.HORIZONTAL, false);
            selected_agents_recyclerView.setLayoutManager(layoutManager1);
            selected_agents_recyclerView.setNestedScrollingEnabled(false);
            filterAgentsAdapter = new FilterAgentsAdapter(FilterActivity.this, selectedAgentList);
            filterAgentsAdapter.selectionMode = true;
            selected_agents_recyclerView.setAdapter(filterAgentsAdapter);


            layoutManager2 = new LinearLayoutManager(FilterActivity.this, LinearLayoutManager.VERTICAL, false);
            property_recyclerView.setLayoutManager(layoutManager2);
            property_recyclerView.setNestedScrollingEnabled(false);
            if (siteList == null) {
                siteList = new ArrayList<>();
            }
            propertyAdapter = new SearchPropertyAdapter(FilterActivity.this, siteList);
            propertyAdapter.selectionMode = true;
            property_recyclerView.setAdapter(propertyAdapter);


            layoutManager4 = new LinearLayoutManager(FilterActivity.this, LinearLayoutManager.HORIZONTAL, false);
            selected_property_recyclerView.setLayoutManager(layoutManager4);
            selected_property_recyclerView.setNestedScrollingEnabled(false);
            filterSitesAdapter = new FilterSitesAdapter(FilterActivity.this, selectedSiteList);
            filterSitesAdapter.selectionMode = true;
            selected_property_recyclerView.setAdapter(filterSitesAdapter);


            if (role != null && !role.trim().isEmpty()) {

                if (Integer.parseInt(role) != Values.UserRoles.AGENT) {
                    getAgentsApi();
                } else {
                    agentsLayout.setVisibility(View.GONE);
                    line.setVisibility(View.GONE);
                }
            }


            agent_search_et.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Utilities.openKeyboard(FilterActivity.this, agent_search_et);
                    agent_search_et.requestFocus();
                    agent_search_et.setCursorVisible(true);
                    return false;
                }
            });


            agent_search_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s != null && s.length() > 0) {
                        if (agentsAdapter != null) {
                            agentsAdapter.search(s.toString());
                        }

                    } else {
                        if (agentsAdapter != null) {
                            agentsAdapter.resetList(agentsList);
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            property_search_et.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Utilities.openKeyboard(FilterActivity.this, property_search_et);
                    property_search_et.requestFocus();
                    property_search_et.setCursorVisible(true);
                    return false;
                }
            });
            property_search_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s != null && s.length() > 0) {
                        propertyAdapter.search(s.toString());
                    } else {
                        propertyAdapter.resetList(siteList);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        try{
        switch (v.getId()) {
            case R.id.actionBack:
                if (actionClose.getVisibility() == View.VISIBLE) {
                    actionClose.performClick();
                } else {
                    onBackPressed();
                }
                break;
            case R.id.actionAgentSelect:
                Helper.getInstance().closeKeyBoard(FilterActivity.this,agent_search_et);
                selectAgents();
                actionClose.performClick();
                break;
            case R.id.actionSiteSelect:
                Helper.getInstance().closeKeyBoard(FilterActivity.this,property_search_et);
                selectSites();
                actionClose.performClick();
                break;
            case R.id.actionClose:
                agent_search_et.setText("");
                property_search_et.setText("");
                mainLayout.setVisibility(View.VISIBLE);
                agentsSearchLayout.setVisibility(View.GONE);
                propertySearchLayout.setVisibility(View.GONE);
                actionClose.setVisibility(View.GONE);
                actionLable.setText("Filters");
                if (selectedAgentList != null && selectedAgentList.size() > 0) {
                    actionClear.setVisibility(View.VISIBLE);
                } else if (selectedSiteList != null && selectedSiteList.size() > 0) {
                    actionClear.setVisibility(View.VISIBLE);
                } else {
                    actionClear.setVisibility(View.GONE);
                }

                break;
            case R.id.agentsLayout:
                setAgentsDataToAdapter();
                if (selectedTempAgentList != null && selectedTempAgentList.size() > 0) {
                    selectedTempAgentList.clear();
                }
                if (selectedAgentList != null && selectedAgentList.size() > 0) {
                    selectedTempAgentList.addAll(selectedAgentList);
                }
                mainLayout.setVisibility(View.GONE);
                agentsSearchLayout.setVisibility(View.VISIBLE);
                propertySearchLayout.setVisibility(View.GONE);
                actionClear.setVisibility(View.GONE);
                actionClose.setVisibility(View.VISIBLE);
                actionLable.setText("Agents");
                break;
            case R.id.propertyLayout:
                setSitesDataToAdapter();
                if (selectedTempSiteList != null && selectedTempSiteList.size() > 0) {
                    selectedTempSiteList.clear();
                }
                if (selectedSiteList != null && selectedSiteList.size() > 0) {
                    selectedTempSiteList.addAll(selectedSiteList);
                }
                mainLayout.setVisibility(View.GONE);
                agentsSearchLayout.setVisibility(View.GONE);
                propertySearchLayout.setVisibility(View.VISIBLE);
                actionClear.setVisibility(View.GONE);
                actionClose.setVisibility(View.VISIBLE);
                actionLable.setText("Properties");
                break;
            case R.id.dateLayout:
                break;
            case R.id.fromDateLayout:
                fromDate();
                break;
            case R.id.toDateLayout:
                toDate();
                break;
            case R.id.actionClear:
                actionClear.setVisibility(View.GONE);
                reset();
                break;
            case R.id.actionApply:
                setResultData();
                break;

        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setResultData() {

        try{
        if (selectedAgentList != null && selectedAgentList.size() > 0) {
            for (int i = 0; i < selectedAgentList.size(); i++) {
                if (i == selectedAgentList.size() - 1) {
                    filter_agent_ids = filter_agent_ids + selectedAgentList.get(i).getUserId();
                } else {
                    filter_agent_ids = filter_agent_ids + selectedAgentList.get(i).getUserId() + ",";
                }

            }
        }

        if (selectedSiteList != null && selectedSiteList.size() > 0) {
            for (int i = 0; i < selectedSiteList.size(); i++) {
                if (i == selectedSiteList.size() - 1) {
                    filter_site_ids = filter_site_ids + selectedSiteList.get(i).getSiteId();
                } else {
                    filter_site_ids = filter_site_ids + selectedSiteList.get(i).getSiteId() + ",";
                }

            }
        }

        callIntent();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callIntent() {
        try {
            Intent intent = new Intent();
            intent.putExtra(Values.IntentData.FILTER_AGENT_IDS, filter_agent_ids);
            intent.putExtra(Values.IntentData.FILTER_SITE_IDS, filter_site_ids);
            intent.putExtra(Values.IntentData.FILTER_FROM_DATE, frmDt);
            intent.putExtra(Values.IntentData.FILTER_TO_DATE, toDt);
            setResult(RESULT_OK, intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setAgentsDataToAdapter() {
        try{
        if (selectedAgentList != null && selectedAgentList.size() > 0 && agentsList != null && agentsList.size() > 0) {
            for (int i = 0; i < selectedAgentList.size(); i++) {
                //  selectedAgentList.get(i).setChecked(true);
                boolean isChecked = selectedAgentList.get(i).isChecked();
                for (int j = 0; j < agentsList.size(); j++) {
                    if (selectedAgentList.get(i).getUserId().equals(agentsList.get(j).getUserId())) {
                        agentsList.get(j).setChecked(true);
                        break;
                    }
                }
            }
            agentsAdapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < agentsList.size(); i++) {
                agentsList.get(i).setChecked(false);
            }
            agentsAdapter.notifyDataSetChanged();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setSitesDataToAdapter() {
        try{
        if (selectedSiteList != null && selectedSiteList.size() > 0 && siteList != null && siteList.size() > 0) {
            for (int i = 0; i < selectedSiteList.size(); i++) {
                boolean isChecked = selectedSiteList.get(i).isChecked();
                for (int j = 0; j < siteList.size(); j++) {
                    if (selectedSiteList.get(i).getSiteId().equals(siteList.get(j).getSiteId())) {
                        siteList.get(j).setChecked(true);
                        break;
                    }
                }
            }
            propertyAdapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < siteList.size(); i++) {
                siteList.get(i).setChecked(false);
            }
            propertyAdapter.notifyDataSetChanged();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void selectAgents() {
        try{
        if (selectedTempAgentList != null && selectedTempAgentList.size() > 0) {
            for (int i = 0; i < selectedTempAgentList.size(); i++) {
                changeStatus(selectedTempAgentList.get(i));
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void selectSites() {
        try{
        if (selectedTempSiteList != null && selectedTempSiteList.size() > 0) {
            for (int i = 0; i < selectedTempSiteList.size(); i++) {
                changePropertyStatus(selectedTempSiteList.get(i));
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void reset() {

        try{
        if (agentsList != null && agentsList.size() > 0) {
            for (int i = 0; i < agentsList.size(); i++) {
                agentsList.get(i).setChecked(false);
            }
        }

        if (siteList != null && siteList.size() > 0) {
            for (int i = 0; i < siteList.size(); i++) {
                siteList.get(i).setChecked(false);
            }
        }

        if (selectedAgentList != null && selectedAgentList.size() > 0) {
            selectedAgentList.clear();
            filterAgentsAdapter.notifyDataSetChanged();
        }

        if (selectedSiteList != null && selectedSiteList.size() > 0) {
            selectedSiteList.clear();
            filterSitesAdapter.notifyDataSetChanged();
        }
        frmDt = "";
        toDt = "";
        filter_agent_ids = "";
        filter_site_ids = "";
        from_date_tv.setText("");
        to_date_tv.setText("");
        isSelectedSites=false;
        isSelectedAgents=false;
        agentsAdapter.notifyDataSetChanged();
        propertyAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    private void fromDate() {

        try{


        Calendar mcurrentDate = Calendar.getInstance();
        int cMonth = mcurrentDate.get(Calendar.MONTH);
        int cYear = mcurrentDate.get(Calendar.YEAR);
        int cDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        mcurrentDate.add(Calendar.MONTH, -3);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        int mYear = mcurrentDate.get(Calendar.YEAR);
        long pastTime = mcurrentDate.getTimeInMillis();


        DatePickerDialog mDatePicker = new DatePickerDialog(
                FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker,
                                  int selectedyear, int selectedmonth,
                                  int selectedday) {
                Calendar mcurrentDate = Calendar.getInstance();
                mcurrentDate.set(Calendar.YEAR, selectedyear);
                mcurrentDate.set(Calendar.MONTH, selectedmonth);
                mcurrentDate.set(Calendar.DAY_OF_MONTH,
                        selectedday);
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

                from_date_tv.setText(sdf.format(mcurrentDate.getTime()));
                to_date_tv.setText(sdf.format(mcurrentDate.getTime()));
                frmDt = sdf1.format(mcurrentDate.getTime());
                toDt = sdf1.format(mcurrentDate.getTime());
                timeInMills = mcurrentDate.getTimeInMillis();


            }
        }, mYear, mMonth, mDay);

        //  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        mDatePicker.getDatePicker().updateDate(cYear, cMonth, cDay);
        mDatePicker.getDatePicker().setMinDate(pastTime);
        mDatePicker.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void toDate() {

        try{
        final Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog mDatePicker = new DatePickerDialog(
                FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker,
                                  int selectedyear, int selectedmonth,
                                  int selectedday) {

                mcurrentDate.set(Calendar.YEAR, selectedyear);
                mcurrentDate.set(Calendar.MONTH, selectedmonth);
                mcurrentDate.set(Calendar.DAY_OF_MONTH,
                        selectedday);
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

                to_date_tv.setText(sdf.format(mcurrentDate.getTime()));
                toDt = sdf1.format(mcurrentDate.getTime());


            }
        }, mYear, mMonth, mDay);

        mDatePicker.getDatePicker().setMinDate(timeInMills);
        mDatePicker.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void changeStatusTemp(AgentInfo activeAgent) {

        try{

        Helper.getInstance().LogDetails("setOnClickListener", "changeStatusTemp called " + " " + activeAgent.isChecked());
        if (selectedTempAgentList != null) {
            if (selectedTempAgentList.size() == 0) {
                selectedTempAgentList.add(activeAgent);
                Helper.getInstance().LogDetails("setOnClickListener", "changeStatusTemp end " + selectedTempAgentList.size());

            } else {
                if (selectedTempAgentList.size() > 0) {
                    boolean isPresent = false;

                    for (int i = 0; i < selectedTempAgentList.size(); i++) {
                        if (selectedTempAgentList.get(i).getUserId() != null && activeAgent.getUserId() != null && selectedTempAgentList.get(i).getUserId().equals(activeAgent.getUserId())) {
                            isPresent = true;
                            if (!activeAgent.isChecked()) {
                                if(!isSelectedAgents)
                                {
                                    selectedTempAgentList.remove(i);
                                }
                                else
                                {
                                    selectedTempAgentList.get(i).setChecked(false);
                                }
                                // selectedTempAgentList.remove(i);

                            }

                            break;
                        }
                    }

                    if (!isPresent) {
                        selectedTempAgentList.add(activeAgent);
                    }

                    Helper.getInstance().LogDetails("setOnClickListener", "changeStatusTemp end " + selectedAgentList.size());

                }


            }
        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void changeStatus(AgentInfo activeAgent) {

        try{

        Helper.getInstance().LogDetails("setOnClickListener", "changeStatus called " + " " + activeAgent.isChecked());
        if (selectedAgentList != null) {
            if (selectedAgentList.size() == 0) {
                selectedAgentList.add(activeAgent);
                Helper.getInstance().LogDetails("setOnClickListener", "changeStatus end " + selectedAgentList.size());
                filterAgentsAdapter.notifyDataSetChanged();
            } else {
                if (selectedAgentList.size() > 0) {
                    isSelectedAgents=true;
                    boolean isPresent = false;

                    for (int i = 0; i < selectedAgentList.size(); i++) {
                        if (selectedAgentList.get(i).getUserId() != null && activeAgent.getUserId() != null && selectedAgentList.get(i).getUserId().equals(activeAgent.getUserId())) {
                            isPresent = true;
                            if (!activeAgent.isChecked()) {
                                selectedAgentList.remove(i);
                            }

                            break;
                        }
                    }

                    if (!isPresent) {
                        selectedAgentList.add(activeAgent);
                    }

                    Helper.getInstance().LogDetails("setOnClickListener", "changeStatus end " + selectedAgentList.size());
                    filterAgentsAdapter.notifyDataSetChanged();
                }


            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void changePropertyStatus(SitesInfo sitesInfo) {

        try{

        Helper.getInstance().LogDetails("setOnClickListener", "changePropertyStatus called " + " " + sitesInfo.isChecked());
        if (selectedSiteList != null) {
            if (selectedSiteList.size() == 0) {

                selectedSiteList.add(sitesInfo);
                Helper.getInstance().LogDetails("setOnClickListener", "changePropertyStatus if end " + selectedSiteList.size());
                filterSitesAdapter.notifyDataSetChanged();
            } else {
                if (selectedSiteList.size() > 0) {
                    isSelectedSites=true;
                    boolean isPresent = false;

                    for (int i = 0; i < selectedSiteList.size(); i++) {
                        if (selectedSiteList.get(i).getSiteId() != null && sitesInfo.getSiteId() != null && selectedSiteList.get(i).getSiteId().equals(sitesInfo.getSiteId())) {
                            isPresent = true;
                            if (!sitesInfo.isChecked()) {
                                selectedSiteList.remove(i);
                            }

                            break;
                        }
                    }

                    if (!isPresent) {
                        selectedSiteList.add(sitesInfo);
                    }

                    Helper.getInstance().LogDetails("setOnClickListener", "changePropertyStatus else end " + selectedSiteList.size());
                    filterSitesAdapter.notifyDataSetChanged();
                }


            }
        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void changePropertyStatusTemp(SitesInfo sitesInfo) {

        try{

        Helper.getInstance().LogDetails("setOnClickListener", "changePropertyStatusTemp called " + " " + sitesInfo.isChecked());
        if (selectedTempSiteList != null) {
            if (selectedTempSiteList.size() == 0) {

                selectedTempSiteList.add(sitesInfo);
                Helper.getInstance().LogDetails("setOnClickListener", "changePropertyStatusTemp end " + selectedTempSiteList.size());

            } else {
                if (selectedTempSiteList.size() > 0) {
                    boolean isPresent = false;

                    for (int i = 0; i < selectedTempSiteList.size(); i++) {
                        if (selectedTempSiteList.get(i).getSiteId() != null && sitesInfo.getSiteId() != null && selectedTempSiteList.get(i).getSiteId().equals(sitesInfo.getSiteId())) {
                            isPresent = true;
                            if (!sitesInfo.isChecked()) {
                                //selectedTempSiteList.remove(i);
                                if(!isSelectedSites)
                                {
                                    selectedTempSiteList.remove(i);
                                }
                                else
                                {
                                    selectedTempSiteList.get(i).setChecked(false);
                                }

                            }

                            break;
                        }
                    }

                    if (!isPresent) {
                        selectedTempSiteList.add(sitesInfo);
                    }

                    Helper.getInstance().LogDetails("setOnClickListener", "changePropertyStatusTemp end " + selectedSiteList.size());

                }


            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getAgentsApi() {

        try{

        Helper.getInstance().LogDetails("getAgentsApi", ApiEndPoint.token + " " + company_token + " " + user_id);
        if (Utilities.getConnectivityStatus(FilterActivity.this) <= 0) {
            Helper.getInstance().pushToast(FilterActivity.this, "Please check your network connection...");
            return;
        }
        openProgess();
        Call<AgentsResponse> call = apiService.getAllAgents(ApiEndPoint.token, company_token, user_id);
        call.enqueue(new Callback<AgentsResponse>() {
            @Override
            public void onResponse(Call<AgentsResponse> call, Response<AgentsResponse> response) {
                AgentsResponse apiResponse = response.body();
                closeProgress();
                if (apiResponse != null) {
                    if (apiResponse.isSuccess()) {

                        if (apiResponse.getData() != null && apiResponse.getData().size() > 0) {

                            agentsList = apiResponse.getData();
                            if (agentsList != null && agentsList.size() > 0) {
                                for (int i = 0; i < agentsList.size(); i++) {
                                    agentsList.get(i).setChecked(false);
                                }
                            }
                            Helper.getInstance().LogDetails("getAgentsApi", "size " + apiResponse.getData().size() + "" + agentsList.size());
                            layoutManager = new LinearLayoutManager(FilterActivity.this, LinearLayoutManager.VERTICAL, false);
                            agents_recyclerView.setLayoutManager(layoutManager);
                            agents_recyclerView.setNestedScrollingEnabled(false);
                            agentsAdapter = new SearchAgentsAdapter(FilterActivity.this, agentsList);
                            agentsAdapter.selectionMode = true;
                            agents_recyclerView.setAdapter(agentsAdapter);
                            //agentsAdapter.notifyDataSetChanged();

                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<AgentsResponse> call, Throwable t) {
                closeProgress();
            }

        });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void progressDialog() {

        try {
            if (!(FilterActivity.this).isFinishing()) {
                dialog = new Dialog(FilterActivity.this, R.style.DialogTheme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.custom_progress_bar);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openProgess() {

        try{
            if (!(FilterActivity.this).isFinishing()) {
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void closeProgress() {
        try
        {
            if (!(FilterActivity.this).isFinishing()) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.cancel();
                    ;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getSharedPreferenceData() {
        try {

            user_id =  Session.getUserID(context);
            company_token =  Session.getCompanyToken(context);
            role =  Session.getUserRole(context);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
