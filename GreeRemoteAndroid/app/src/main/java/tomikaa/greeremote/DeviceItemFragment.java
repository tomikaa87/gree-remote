package tomikaa.greeremote;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tomikaa.greeremote.Gree.Device.Device;
import tomikaa.greeremote.Gree.Device.DeviceManager;
import tomikaa.greeremote.Gree.Device.DeviceManagerEventListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DeviceItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private DeviceManagerEventListener mDeviceManagerEventListener;

    private View mView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeviceItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DeviceItemFragment newInstance(int columnCount) {
        DeviceItemFragment fragment = new DeviceItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deviceitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            mView = view;

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            DeviceManager dm = DeviceManager.getInstance();
            dm.unregisterEventListener(mDeviceManagerEventListener);

            mDeviceManagerEventListener = new DeviceManagerEventListener() {
                @Override
                public void onEvent(Event event) {
                if (event == Event.DEVICE_LIST_UPDATED || event == Event.DEVICE_STATUS_UPDATED)
                    updateDeviceList();
                }
            };

            dm.registerEventListener(mDeviceManagerEventListener);

            updateDeviceList();

            DividerItemDecoration dividerDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    LinearLayoutManager.VERTICAL);
            recyclerView.addItemDecoration(dividerDecoration);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        DeviceManager.getInstance().unregisterEventListener(mDeviceManagerEventListener);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DeviceItem item);
    }

    private void updateDeviceList() {
        if (mView == null || !(mView instanceof RecyclerView))
            return;

        final List<DeviceItem> items = new ArrayList<>();

        for (Device d : DeviceManager.getInstance().getDevices()) {
            items.add(new DeviceItem(d));
        }

        RecyclerView recyclerView = (RecyclerView) mView;

        recyclerView.setAdapter(new MyDeviceItemRecyclerViewAdapter(items, mListener));
    }
}
