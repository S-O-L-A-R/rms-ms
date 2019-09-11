package com.solar.ms.rms.config;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Component
@Profile({"local", "ut", "deployment"})
public class FirestoreMock implements Firestore {
    @Nonnull
    @Override
    public CollectionReference collection(@Nonnull String s) {
        return null;
    }

    @Nonnull
    @Override
    public DocumentReference document(@Nonnull String s) {
        return null;
    }

    @Nonnull
    @Override
    public Iterable<CollectionReference> listCollections() {
        return null;
    }

    @Nonnull
    @Override
    public Iterable<CollectionReference> getCollections() {
        return null;
    }

    @Override
    public Query collectionGroup(@Nonnull String s) {
        return null;
    }

    @Nonnull
    @Override
    public <T> ApiFuture<T> runTransaction(@Nonnull Transaction.Function<T> function) {
        return null;
    }

    @Nonnull
    @Override
    public <T> ApiFuture<T> runTransaction(@Nonnull Transaction.Function<T> function, @Nonnull TransactionOptions transactionOptions) {
        return null;
    }

    @Nonnull
    @Override
    public ApiFuture<List<DocumentSnapshot>> getAll(@Nonnull DocumentReference... documentReferences) {
        return null;
    }

    @Nonnull
    @Override
    public ApiFuture<List<DocumentSnapshot>> getAll(@Nonnull DocumentReference[] documentReferences, @Nullable FieldMask fieldMask) {
        return null;
    }

    @Nonnull
    @Override
    public WriteBatch batch() {
        return null;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public FirestoreOptions getOptions() {
        return null;
    }
}
