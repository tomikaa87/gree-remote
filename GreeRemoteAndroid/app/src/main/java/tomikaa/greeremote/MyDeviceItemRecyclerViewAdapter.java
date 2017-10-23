package tomikaa.greeremote;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tomikaa.greeremote.DeviceItemFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DeviceItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDeviceItemRecyclerViewAdapter extends RecyclerView.Adapter<MyDeviceItemRecyclerViewAdapter.ViewHolder> {

    private final List<DeviceItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyDeviceItemRecyclerViewAdapter(List<DeviceItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_deviceitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(holder.mItem.mName);
        holder.mTemperatureView.setText(String.format("%d °C", holder.mItem.mTemperature));

        switch (holder.mItem.mMode)
        {
            case AUTO:
                holder.mModeView.setText(R.string.mode_auto);
                break;

            case COOL:
                holder.mModeView.setText(R.string.mode_cool);
                break;

            case DRY:
                holder.mModeView.setText(R.string.mode_dry);
                break;

            case HEAT:
                holder.mModeView.setText(R.string.mode_heat);
                break;

            case FAN:
                holder.mModeView.setText(R.string.mode_fan);
                break;
        }

        switch (holder.mItem.mRoomType)
        {
            case BEDROOM:
                holder.mIconView.setImageResource(R.mipmap.ic_bedroom);
                break;

            case LIVING_ROOM:
                holder.mIconView.setImageResource(R.mipmap.ic_livingroom);
                break;

            case KITCHEN:
                holder.mIconView.setImageResource(R.mipmap.ic_kitchen);
                break;

            case DINING_ROOM:
                holder.mIconView.setImageResource(R.mipmap.ic_diningroom);
                break;

            case BATHROOM:
                holder.mIconView.setImageResource(R.mipmap.ic_bathroom);
                break;

            case OFFICE:
                holder.mIconView.setImageResource(R.mipmap.ic_office);
                break;

            default:
                holder.mIconView.setImageResource(R.mipmap.ic_air_conditioner);
                break;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final ImageView mIconView;
        public final TextView mModeView;
        public final TextView mTemperatureView;
        public DeviceItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.nameTextView);
            mIconView = view.findViewById(R.id.icon);
            mModeView = view.findViewById(R.id.modeTextView);
            mTemperatureView = view.findViewById(R.id.temperatureTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
