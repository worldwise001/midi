package sh.shh.midi.roland;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

@AutoValue
abstract class VAccordionSet {
    static VAccordionSet create(VAccordionSetBlob sc,
                                VAccordionSetBlob o_r,
                                VAccordionSetBlob ob_r,
                                VAccordionSetBlob obc_r,
                                VAccordionSetBlob ofb_r,
                                VAccordionSetBlob tr,
                                VAccordionSetBlob br,
                                VAccordionSetBlob bcr,
                                VAccordionSetBlob orr,
                                VAccordionSetBlob obr,
                                VAccordionSetBlob obcr,
                                VAccordionSetBlob ofbr,
                                @Nullable VAccordionSetBlob orch1,
                                @Nullable VAccordionSetBlob orch2,
                                @Nullable VAccordionSetBlob sc2) {
        return new AutoValue_VAccordionSet(sc, o_r, ob_r, obc_r, ofb_r, tr, br, bcr, orr, obr, obcr, ofbr,
                orch1, orch2, sc2);
    }

    abstract VAccordionSetBlob sc();
    abstract VAccordionSetBlob o_r();
    abstract VAccordionSetBlob ob_r();
    abstract VAccordionSetBlob obc_r();
    abstract VAccordionSetBlob ofb_r();
    abstract VAccordionSetBlob tr();
    abstract VAccordionSetBlob br();
    abstract VAccordionSetBlob bcr();
    abstract VAccordionSetBlob orr();
    abstract VAccordionSetBlob obr();
    abstract VAccordionSetBlob obcr();
    abstract VAccordionSetBlob ofbr();
    @Nullable abstract VAccordionSetBlob orch1();
    @Nullable abstract VAccordionSetBlob orch2();
    @Nullable abstract VAccordionSetBlob sc2();
}
