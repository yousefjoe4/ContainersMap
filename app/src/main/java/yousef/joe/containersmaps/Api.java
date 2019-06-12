package yousef.joe.containersmaps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import yousef.joe.containersmaps.model.Container;

public class Api {

    public interface ContainersReceived {
        void onDataReceived(List<Container> containersList);
    }

    public interface UpdateContainer {
        void onSuccess();
    }

    private static CollectionReference collectionReference =
            FirebaseFirestore.getInstance().collection("Containers");

    public static void uploadDummyContiners() {
        // Get dummy data
        List<Container> dummyContainers = Utils.generateDummyContainers();

        // Upload dummy data to cloud
        uploadContainers(dummyContainers);
    }


    public static void getContainers(final Context context, final Api.ContainersReceived dataReceived) {

        // Get data from Firestore
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                final List<Container> containerList = new ArrayList<>();

                if (queryDocumentSnapshots != null) {

                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();

                    // Iterate over each document and convert it into a Container item
                    for (DocumentSnapshot document : data) {
                        // Database object into Container
                        Container container = document.toObject(Container.class);
                        containerList.add(container);
                    }

                    // After all the containers have been added to the list pass it to the interface
                    dataReceived.onDataReceived(containerList);

                } else {
                    // Send Telemetries
                    Bundle bundle = new Bundle();
                    bundle.putString("getContainers", " getting documents has failed, it's null");
                    FirebaseAnalytics.getInstance(context).logEvent("Api",bundle);
                }

            }
        });


    }

    private static void uploadContainers(List<Container> containers) {
        for (Container container : containers) {
            collectionReference.document(String.valueOf(container.getContainerId())).set(container);
        }

    }

    public static void updateContainer(final Context context, Container container,
                                       final UpdateContainer updateContainer) {

        String containerId = String.valueOf(container.getContainerId());
        collectionReference.document(containerId).set(container).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateContainer.onSuccess();
                        } else {

                            Toast.makeText(context, context.getString(R.string.unkown_error)
                                    , Toast.LENGTH_LONG).show();
                            // Send Telemetries
                            Bundle bundle = new Bundle();
                            bundle.putString("updateContainer", " updating container has failed");
                            FirebaseAnalytics.getInstance(context).logEvent("Api",bundle);
                        }
                    }
                });

    }
}
