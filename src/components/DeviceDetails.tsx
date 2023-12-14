import {
    IonCol,
    IonRow,
    IonCard,
    IonButton,
    IonText,
    IonModal,
    IonContent,
    IonHeader,
    IonToolbar,
    IonButtons,
    IonItem,
    IonTitle,
    IonLoading,
} from "@ionic/react";
import "./DeviceDetails.css";
import React, { useState, useRef, useEffect } from "react";
import { OverlayEventDetail } from "@ionic/react/dist/types/components/react-component-lib/interfaces";
import { IncasaBLE } from "../plugins/IncasaBLE";

interface ContainerProps {
    name: String;
    type: String;
}

const DeviceDetails: React.FC<ContainerProps> = (device: ContainerProps) => {
    const [isPairing, setIsPairing] = useState(false);
    const [isPaired, setIsPaired] = useState(false);
    const [measure, setMeasure] = useState(0 as Number);

    const modal = useRef<HTMLIonModalElement>(null);
    const loading = useRef<HTMLIonLoadingElement>(null);

    function confirm() {
        setIsPaired(!isPaired);
        modal.current?.dismiss(false);
    }

    function onWillDismiss(ev: CustomEvent<OverlayEventDetail>) {
        console.log(ev.detail.data);
        setIsPairing(ev.detail.data);
    }

    function setDevice(deviceName: String) {
        alert(deviceName);
        IncasaBLE.startDevice({ deviceName: deviceName }).then(() => {
            setIsPaired(true);
        });
        setIsPairing(true);
    }

    useEffect(() => {
        IncasaBLE.addListener("measureChange", (measure) => {
            console.log(measure);
            setMeasure(measure);
        });
        IncasaBLE.addListener("pairingChange", (paired) => {
            console.log(paired);
            setIsPaired(paired);
        });
    }, []);

    return (
        <div>
            <IonRow className="ion-align-items-start">
                <IonCol>
                    <IonCard>
                        <IonRow class="ion-justify-content-end">
                            <IonCol>
                                <IonRow>
                                    <IonCol>
                                        <IonText>
                                            <div style={{ textAlign: "start", fontSize: "1.1em", fontWeight: "bold" }}>
                                                {device.name}
                                            </div>
                                        </IonText>
                                    </IonCol>
                                </IonRow>
                                <IonRow>
                                    <IonCol>
                                        <div style={{ textAlign: "start" }}>{device.type}</div>
                                    </IonCol>
                                </IonRow>
                            </IonCol>
                            <IonCol class="ion-align-self-center">
                                <IonRow>
                                    <IonCol>
                                        <div style={{ textAlign: "end" }}>
                                            <IonButton onClick={() => setDevice(device.name)}>
                                                {isPaired ? "UnPair" : "Pair"}
                                            </IonButton>
                                        </div>
                                    </IonCol>
                                </IonRow>
                            </IonCol>
                        </IonRow>
                    </IonCard>
                </IonCol>
            </IonRow>
            <IonModal isOpen={isPairing} ref={modal} onWillDismiss={(ev) => onWillDismiss(ev)}>
                <IonHeader>
                    <IonToolbar>
                        <IonButtons slot="start">
                            <IonButton onClick={() => modal.current?.dismiss(false)}>Cancel</IonButton>
                        </IonButtons>
                        <IonButtons slot="end">
                            <IonButton strong={true} onClick={() => confirm()}>
                                Confirm
                            </IonButton>
                        </IonButtons>
                    </IonToolbar>
                </IonHeader>
                <IonContent className="ion-padding">
                    {isPaired ? (
                        <div>
                            <div>{device.name}</div>
                            <div>{measure == 0 ? "Measuring..." : measure.toString()}</div>
                        </div>
                    ) : (
                        <div>
                            <div>{device.name}</div>
                            <div>Pairing...</div>
                        </div>
                    )}
                </IonContent>
            </IonModal>
        </div>
    );
};

export default DeviceDetails;
