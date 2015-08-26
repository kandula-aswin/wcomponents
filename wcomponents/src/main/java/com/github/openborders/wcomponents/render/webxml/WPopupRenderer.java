package com.github.openborders.wcomponents.render.webxml;

import com.github.openborders.wcomponents.Renderer;
import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WPopup;
import com.github.openborders.wcomponents.XmlStringBuilder;
import com.github.openborders.wcomponents.servlet.WebXmlRenderContext;
import com.github.openborders.wcomponents.util.Util;

/**
 * The {@link Renderer} for {@link WPopup}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WPopupRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WPopup.
     * 
     * @param component the WPopup to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WPopup popup = (WPopup) component;
        XmlStringBuilder xml = renderContext.getWriter();
        int width = popup.getWidth();
        int height = popup.getHeight();
        String targetWindow = popup.getTargetWindow();
        
        xml.appendTagOpen("ui:popup");
        xml.appendAttribute("url", popup.getUrl());
        xml.appendOptionalAttribute("width", width > 0, width);
        xml.appendOptionalAttribute("height", height > 0, height);
        xml.appendOptionalAttribute("resizable", popup.isResizable(), "true");
        xml.appendOptionalAttribute("showScrollbars", popup.isScrollable(), "true");
        xml.appendOptionalAttribute("targetWindow", !Util.empty(targetWindow), targetWindow);
        xml.appendClose();
        xml.appendEndTag("ui:popup");
    }
}