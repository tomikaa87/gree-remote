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
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            final List<DeviceItem> items = new ArrayList<>();
            items.add(new DeviceItem());
            items.add(new DeviceItem());
            items.add(new DeviceItem());
            items.add(new DeviceItem());

            items.get(0).mName = "Living room";
            items.get(0).mMode = DeviceItem.Mode.COOL;
            items.get(0).mRoomType = DeviceItem.RoomType.LIVING_ROOM;
            items.get(0).mTemperature = 24;

            items.get(1).mName = "Bedroom";
            items.get(1).mMode = DeviceItem.Mode.FAN;
            items.get(1).mRoomType = DeviceItem.RoomType.BEDROOM;
            items.get(1).mTemperature = 25;

            items.get(2).mName = "Dining room";
            items.get(2).mMode = DeviceItem.Mode.AUTO;
            items.get(2).mRoomType = DeviceItem.RoomType.DINING_ROOM;
            items.get(2).mTemperature = 23;

            items.get(3).mName = "Kitchen";
            items.get(3).mMode = DeviceItem.Mode.COOL;
            items.get(3).mRoomType = DeviceItem.RoomType.KITCHEN;
            items.get(3).mTemperature = 23;

            recyclerView.setAdapter(new MyDeviceItemRecyclerViewAdapter(items, mListener));

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
}
