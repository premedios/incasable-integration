import {
    IonContent,
    IonHeader,
    IonPage,
    IonTitle,
    IonToolbar,
    IonButton,
    IonGrid,
    IonRow,
    IonCol,
    IonModal,
} from "@ionic/react";
import DeviceDetails from "../components/DeviceDetails";
import { IncasaBLE } from "../plugins/IncasaBLE";
import React, { useEffect, useState } from "react";
import "./Home.css";

const Home: React.FC = () => {
    const [verifiedPermissions, setVerifiedPermissions] = useState(false);
    const mainStyle: React.CSSProperties = {
        width: "100vw",
        textAlign: "center",
    };

    useEffect(() => {
        IncasaBLE.verifyRequiredPermissions().then((result) => setVerifiedPermissions(result.verified));
    }, []);

    return (
        <IonPage>
            <IonHeader>
                <IonToolbar>
                    <IonTitle>kDoc Integration</IonTitle>
                </IonToolbar>
            </IonHeader>
            <IonContent fullscreen>
                <IonHeader collapse="condense">
                    <IonToolbar>
                        <IonTitle size="large">kDoc Integration - {verifiedPermissions}</IonTitle>
                    </IonToolbar>
                </IonHeader>
                <div style={mainStyle}>
                    <IonGrid>
                        <DeviceDetails name="A&D UA-651" type="Blood Pressure" />
                        <DeviceDetails name="A&D UC-352" type="Weight" />
                        <DeviceDetails name="A&D UT-201" type="Temperature" />
                        <DeviceDetails name="Nonin 3230" type="Oximetry" />
                        <DeviceDetails name="Activ8 A8015" type="Activity" />
                        <DeviceDetails name="Activ8 A8016" type="Activity" />
                        <DeviceDetails name="Wellue DuoEK" type="ECG" />
                    </IonGrid>
                </div>
            </IonContent>
        </IonPage>
    );
};

export default Home;
