package de.wackernagel.essbar.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.room.Customer;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

public class CustomerListAdapter extends ListAdapter<Customer, CustomerListAdapter.CustomerViewHolder> {

    @Nullable
    private OnCustomerClickListener customerClickListener;

    CustomerListAdapter() {
        super( new CustomerItemCallback() );
        setHasStableIds( true );
    }

    @Override
    public long getItemId(int position) {
        return getItem( position ).getId();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerViewHolder( LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_customer, parent, false ) );
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerViewHolder holder, final int position) {
        holder.bindCustomer( getItem( position ) );
        holder.itemView.setOnClickListener( v -> {
            final int adapterPosition = holder.getAdapterPosition();
            if( adapterPosition != NO_POSITION && customerClickListener != null ) {
                customerClickListener.onCustomerClick( getItem( adapterPosition ) );
            }
        } );
    }

    void setOnCustomerClickListener( @Nullable final OnCustomerClickListener customerClickListener) {
        this.customerClickListener = customerClickListener;
    }

    interface OnCustomerClickListener {
        void onCustomerClick( Customer customer );
    }

    static class CustomerItemCallback extends DiffUtil.ItemCallback<Customer> {

        @Override
        public boolean areItemsTheSame(@NonNull Customer oldItem, @NonNull Customer newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Customer oldItem, @NonNull Customer newItem) {
            return TextUtils.equals( oldItem.getNumber(), newItem.getNumber() ) &&
                    TextUtils.equals( oldItem.getEncryptedPassword(), newItem.getEncryptedPassword() ) &&
                    TextUtils.equals( oldItem.getEncryptionIv(), newItem.getEncryptionIv() ) &&
                    TextUtils.equals( oldItem.getName(), newItem.getName() );
        }
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView numberTextView;

        CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            numberTextView = itemView.findViewById(R.id.numerTextView);
        }

        void bindCustomer( final Customer customer ) {
            nameTextView.setText( customer.getName() );
            numberTextView.setText( customer.getNumber() );
        }
    }
}
