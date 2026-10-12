package club.rainbowkitty.kalic0re.common.portal;

import eu.pb4.polymer.virtualentity.api.ElementHolder;

// The display over one portal block, attached to the block by Polymer: one MaterialElement,
// static, never ticked.
final class PortalHolder extends ElementHolder {
    PortalHolder(PortalMaterial material, PortalPlane plane) {
        addElement(new MaterialElement(material, plane));
    }
}
