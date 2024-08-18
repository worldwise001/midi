package sh.shh.midi.roland;

import com.google.auto.value.AutoValue;
import org.w3c.dom.Element;

@AutoValue
abstract class VAccordionSetBlob {
    static VAccordionSetBlob create(int number, int offset, int size) {
        return new AutoValue_VAccordionSetBlob(number, offset, size);
    }
    static VAccordionSetBlob create(Element element) {
        return new AutoValue_VAccordionSetBlob(
                Integer.parseInt(element.getAttribute("number")),
                Integer.parseInt(element.getAttribute("offset")),
                Integer.parseInt(element.getAttribute("size")));
    }

    abstract int number();
    abstract int offset();
    abstract int size();
}
