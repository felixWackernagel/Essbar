package de.wackernagel.essbar.room;

import java.util.Objects;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import de.wackernagel.essbar.ui.lists.Listable;

@Entity( tableName = "customers",
         indices = { @Index( value = "number", unique = true ) } )
public class Customer implements Listable {
    @PrimaryKey( autoGenerate = true)
    private long id;

    @NonNull
    private String number;

    @NonNull
    @ColumnInfo(name = "encryption_iv")
    private String encryptionIv;

    @NonNull
    @ColumnInfo(name = "encrypted_password")
    private String encryptedPassword;

    @NonNull
    private String name;

    @Ignore
    public Customer( @NonNull final String number, @NonNull final String encryptionIv, @NonNull final String encryptedPassword, @NonNull final String name ) {
        this.number = number;
        this.encryptionIv = encryptionIv;
        this.encryptedPassword = encryptedPassword;
        this.name = name;
    }

    Customer( final long id, @NonNull final String number, @NonNull final String encryptionIv, @NonNull final String encryptedPassword, @NonNull final String name ) {
        this.id = id;
        this.number = number;
        this.encryptionIv = encryptionIv;
        this.encryptedPassword = encryptedPassword;
        this.name = name;
    }

    @Override
    public long getId() {
        return id;
    }

    @NonNull
    public String getNumber() {
        return number;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    @NonNull
    public String getEncryptionIv() {
        return encryptionIv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id &&
                Objects.equals(number, customer.number) &&
                Objects.equals(encryptionIv, customer.encryptionIv) &&
                Objects.equals(encryptedPassword, customer.encryptedPassword) &&
                Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, number, encryptionIv, encryptedPassword, name);
    }

    @Override
    @Nonnull
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", encryptionIv='" + encryptionIv + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
