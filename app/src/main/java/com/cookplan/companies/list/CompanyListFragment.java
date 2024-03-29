package com.cookplan.companies.list;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cookplan.BaseFragment;
import com.cookplan.R;
import com.cookplan.companies.list.CompanyListRecyclerAdapter.CompanyListEventListener;
import com.cookplan.geofence.GeoFencePresenter;
import com.cookplan.geofence.GeoFencePresenterImpl;
import com.cookplan.geofence.GeoFenceView;
import com.cookplan.models.Company;
import com.cookplan.utils.PermissionUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static com.cookplan.utils.Constants.GEOFENCE_DEFAULT_PERIOD_MILLISECONDS;
import static com.cookplan.utils.Constants.GEOFENCE_DEFAULT_RADIUS_IN_METERS;
import static com.cookplan.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE;


public class CompanyListFragment extends BaseFragment implements CompanyListView, GeoFenceView {


    private boolean mPermissionDenied = false;
    private CompanyListPresenter presenter;
    private GeoFencePresenter geoFencePresenter;

    private OnPointsListClickListener clickListener;
    private CompanyListRecyclerAdapter adapter;
    private View mainView;

    public CompanyListFragment() {
    }


    public static CompanyListFragment newInstance() {
        CompanyListFragment fragment = new CompanyListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        presenter = new CompanyListPresenterImpl(this);
        geoFencePresenter = new GeoFencePresenterImpl(getActivity(), this);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermissionFromFragment(this, LOCATION_PERMISSION_REQUEST_CODE,
                                                          android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            if (presenter != null) {
                presenter.getUsersCompanyList();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                                                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions();
        } else {
            mPermissionDenied = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.ErrorDialog
                .newInstance("showMissingPermissionError").show(getActivity().getFragmentManager(), "dialog");
    }

    @Override
    public void onStart() {
        super.onStart();
        requestPermissions();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.onStop();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_company_list, container, false);

        RecyclerView companyListRecyclerView = (RecyclerView) mainView.findViewById(R.id.company_list_recycler);
        companyListRecyclerView.setHasFixedSize(false);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        companyListRecyclerView.setLayoutManager(manager);


        CompanyListEventListener eventListener = new CompanyListEventListener() {
            @Override
            public void onCompanyClick(Company company) {
                if (clickListener != null) {
                    clickListener.onClick(company);
                }
            }

            @Override
            public void onCompanyGeoFenceIconClick(Company company) {
                if (geoFencePresenter != null) {
                    if (company.isAddedToGeoFence()) {
                        geoFencePresenter.removeGeoFence(company);
                    } else {
                        geoFencePresenter.setGeoFence(company, GEOFENCE_DEFAULT_RADIUS_IN_METERS, GEOFENCE_DEFAULT_PERIOD_MILLISECONDS);
                    }
                }
            }

            @Override
            public void onCompanyLongClick(Company company) {
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                        .setTitle(R.string.attention_title)
                        .setMessage(R.string.are_you_sure_remove_company)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            dialog.dismiss();
                            if (presenter != null) {
                                presenter.removeCompany(company);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        };

        adapter = new CompanyListRecyclerAdapter(new ArrayList<>(), eventListener);
        companyListRecyclerView.setAdapter(adapter);

        companyListRecyclerView.setRecyclerListener(viewHolder -> {
            if (viewHolder instanceof CompanyListRecyclerAdapter.ViewHolder) {
                CompanyListRecyclerAdapter.ViewHolder holder = (CompanyListRecyclerAdapter.ViewHolder) viewHolder;
                if (holder.map != null) {
                    // Clear the map and free up resources by changing the map type to none
                    holder.map.clear();
                    holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
                }
            }
        });

        return mainView;
    }

    public void setOnCompanyClickListener(OnPointsListClickListener _listener) {
        clickListener = _listener;
    }

    public List<Company> getValues() {
        return adapter != null ? adapter.getValues() : new ArrayList<>();
    }

    @Override
    public void setCompanyList(List<Company> companyList) {
        ProgressBar progressBar = (ProgressBar) mainView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        setEmptyViewVisability(View.GONE);
        setRecyclerViewVisability(View.VISIBLE);
        if (adapter != null) {
            adapter.updateItems(companyList);
        }
    }

    @Override
    public void setEmptyView() {
        ProgressBar progressBar = (ProgressBar) mainView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        setEmptyViewVisability(View.VISIBLE);
        setRecyclerViewVisability(View.GONE);
    }

    private void setRecyclerViewVisability(int visability) {
        View recyclerView = mainView.findViewById(R.id.product_list_recycler);
        if (recyclerView != null) {
            recyclerView.setVisibility(visability);
        }
    }

    @Override
    public void setGeofenceAddedSuccessfull() {
        View view = mainView.findViewById(R.id.main_view);
        if (view != null) {
            Snackbar.make(view, R.string.company_added_to_geofence_for_10_days_message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void setGeoFenceError(int errorResourceId) {
        View view = mainView.findViewById(R.id.main_view);
        if (view != null) {
            Snackbar.make(view, getString(errorResourceId), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void setGeofenceRemovedSuccessfull() {
        View view = mainView.findViewById(R.id.main_view);
        if (view != null) {
            Snackbar.make(view, R.string.geofence_notification_turned_off, Snackbar.LENGTH_LONG).show();
        }
    }

    public interface OnPointsListClickListener {
        void onClick(Company item);
    }
}
