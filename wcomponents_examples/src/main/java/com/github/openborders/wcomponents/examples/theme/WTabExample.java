package com.github.openborders.wcomponents.examples.theme;

import java.util.Date;
import java.util.List;

import com.github.openborders.wcomponents.DefaultWComponent;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.WCheckBox;
import com.github.openborders.wcomponents.WDecoratedLabel;
import com.github.openborders.wcomponents.WHeading;
import com.github.openborders.wcomponents.WHorizontalRule;
import com.github.openborders.wcomponents.WImage;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.WSeparator;
import com.github.openborders.wcomponents.WTab;
import com.github.openborders.wcomponents.WTabGroup;
import com.github.openborders.wcomponents.WTabSet;
import com.github.openborders.wcomponents.WText;
import com.github.openborders.wcomponents.WTabSet.TabMode;
import com.github.openborders.wcomponents.WTabSet.TabSetType;
import com.github.openborders.wcomponents.subordinate.Equal;
import com.github.openborders.wcomponents.subordinate.Hide;
import com.github.openborders.wcomponents.subordinate.Rule;
import com.github.openborders.wcomponents.subordinate.Show;
import com.github.openborders.wcomponents.subordinate.WSubordinateControl;

import com.github.openborders.wcomponents.examples.common.ExplanatoryText;

/**
 * Examples of properties of {@lin kWTab}. For properties of {@link WTabSet} see {@link WTabSetExample}.
*
* @author exbtma
 */
public class WTabExample extends WPanel
{
    /** Sample Long Text. */
    private static final String LONG_TEXT = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent lectus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Phasellus et turpis. Aenean convallis eleifend elit. Donec venenatis justo id nunc. Sed at purus vel quam mattis elementum. Sed ultrices lobortis orci. Pellentesque enim urna, volutpat at, sagittis id, faucibus sed, lectus. Integer dapibus nulla semper mi. Nunc posuere molestie augue. Aliquam varius libero in tortor. Sed nibh. Nunc erat nunc, pellentesque at, sodales vel, dapibus sit amet, tortor.";

    /** a tab with dynamic content */
    private final WTab tabset1TabDynamic;
    /** a tab with lazy content */
    private final WTab tabset1TabLazy;
    /** a tab with eager content */
    private final WTab tabset1TabEager;
    /** a tab with server content */
    private final WTab tabset1TabServer;
    /** a tab with client content */
    private final WTab tabset1TabClient;
    
    /** some explanatory text */
    private final ExplanatoryText explanationWithTimeStamp;
    /**
     * Display various tabs to show each property and option.
     */
    public WTabExample()
    {
        super();
        add(new WHeading(WHeading.MAJOR, "Examples of WTab properties"));
        add(new WHeading(WHeading.SECTION, "Tab Modes."));
        explanationWithTimeStamp = new ExplanatoryText("The tabs in the following example each display the time at which the tab content was fetched. The time the page was rendered was " + (new Date()).toString());
        add(explanationWithTimeStamp);
        
        
        WTabSet tabset1 = new WTabSet();
        tabset1.addTab(new ExplanatoryText("This is the content of tab one."),"Client", TabMode.CLIENT);
        tabset1TabServer = tabset1.addTab(new WText(""),"Server", TabMode.SERVER);
        tabset1TabLazy = tabset1.addTab(new WText(""),"Lazy", TabMode.LAZY);
        tabset1TabDynamic = tabset1.addTab(new WText(""),"Dynamic", TabMode.DYNAMIC);
        tabset1TabEager = tabset1.addTab(new WText(""),"Eager", TabMode.EAGER);
        tabset1TabClient = tabset1.addTab(new ExplanatoryText("This content was present when the page first rendered."),"Another Client", TabMode.CLIENT);
        tabset1.setMargin(new com.github.openborders.wcomponents.Margin(0, 0, 24, 0));
        add(tabset1);

        add(new WHeading(WHeading.SECTION, "One tab disabled."));
        WTabSet tabset2 = new SampleTabSet();
        /*NOTE: you should do a null check on the tab but we are taking a shortcut knowing the structure of the tabset */
        tabset2.getTab(1).setDisabled(true);
        tabset2.getTab(1).setText("Disabled tab");
        add(tabset2);
        add(new WHorizontalRule());

        add(new WHeading(WHeading.SECTION, "Active tab."));
        add(new ExplanatoryText("If a tab is not set active explicitly then the first visible tab is open by default unless the user has previously set a different tab open in this session."));
        
        WTabSet tabset3 = new SampleTabSet(1);
        add(tabset3);
        //tabset3.setActiveIndex(1);
        /* Instead of using setActiveIndex it is possible to set the active tab using the tab content. 
         * TODO: this is stupid! setActiveTab should use the WTab not its content! */
        /*tabset3.setActiveTab(tabset3.getTab(1).getContent());*/


        add(new WHeading(WHeading.SECTION, "Active tab disabled."));
        WTabSet tabset4 = new SampleTabSet(1);
        add(tabset4);
        tabset4.getTab(tabset4.getActiveIndex()).setDisabled(true);
        tabset4.getTab(tabset4.getActiveIndex()).setText("Active and disabled tab");
        
        add(new WHorizontalRule());
        
        /* NOTE: if the WTabSet is of TYPE_ACCORDION then several tabs may be open at the same time. */
        

        add(new WHeading(WHeading.SECTION, "Tabs with access keys"));
        WTabSet tabset5 = new WTabSet();
        add(tabset5);
        tabset5.setMargin(new com.github.openborders.wcomponents.Margin(0, 0, 24, 0));
        //It would be normal to use the first letter of the tab text as the access key but 'F' is problematic for some browsers.
        tabset5.addTab(new ExplanatoryText("Some content should go into here."), "First tab", WTabSet.TAB_MODE_CLIENT, 'T');
        tabset5.addTab(new ExplanatoryText(LONG_TEXT), "Second tab", WTabSet.TAB_MODE_CLIENT, 'S');
        //access key does not have to be in the tab's text label
        tabset5.addTab(new ExplanatoryText("Some other content should go into here."), "Third tab", WTabSet.TAB_MODE_CLIENT, 'X');
        
        /*
         * NOTE: WTab is not a subordinate target so we cannot hide it. This is contrary to the schema
         * TODO: fix this by either making WTab a subordinate target OR by removing the attribute from 
         * the schema
         * 
         *  NOTE: this being the case we cannot use a subordinate to enable a disabled tab.
         *  
         *  
        add(new WHorizontalRule());
        add(new WHeading(WHeading.SECTION, "Hiding a WTab with a subordinate control"));
        WTabSet tabset6 = new SampleTabSet();
        add(tabset6);
        tabset6.setMargin(new com.github.openborders.wcomponents.Margin(0));
        final WCheckBox controller = new WCheckBox();
        add(controller);
        controller.setToolTip("Hide the first tab");
        controller.setSelected(true);
        WSubordinateControl sub = new WSubordinateControl();
        add(sub);
        Rule rule = new Rule();
        rule.setCondition(new Equal(controller, Boolean.TRUE.toString()));
        rule.addActionOnTrue(new Hide(tabset6.getTab(0)));
        rule.addActionOnFalse(new SHow(tabset6.getTab(0)));
        sub.addRule(rule);
        //tabset6.getTab(0).setHidden(true);
         
         */
        add(new WHeading(WHeading.SECTION, "Creating a Tab with non-text content."));
        WTabSet tabset7 = new WTabSet();
        add(tabset7);

        WImage imageTab1 = new WImage("/image/attachment.png", "Attachments");
        imageTab1.setCacheKey("eg-tab-image-1");
        WImage imageTab2 = new WImage("/image/error.png", "Errors");
        imageTab2.setCacheKey("eg-tab-image-2");
        WImage imageTab3 = new WImage("/image/flag.png", "Flags");
        imageTab3.setCacheKey("eg-tab-image-3");
        WImage imageTab4 = new WImage("/image/help.png", "Help");
        imageTab4.setCacheKey("eg-tab-image-4");

        tabset7.addTab(new WText("Some content for the attachments tab"), new WDecoratedLabel(imageTab1, new WText("Attachments"), null),
                       WTabSet.TAB_MODE_CLIENT, '1');

        tabset7.addTab(new WText("Some content for the errors tab"), new WDecoratedLabel(imageTab2, new WText("Error List"), null),
                      WTabSet.TAB_MODE_CLIENT, '2');

        tabset7.addTab(new WText("Some content for the flagged tab"), new WDecoratedLabel(imageTab3, new WText("Flagged Items"), null),
                      WTabSet.TAB_MODE_CLIENT, '3');
        tabset7.addTab(new WText("Some content for the help tab"), new WDecoratedLabel(imageTab4),
                       WTabSet.TAB_MODE_CLIENT, '4');
        }
    

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        explanationWithTimeStamp.setText("The time the page was rendered was " + (new Date()).toString());
        tabset1TabClient.setContent(new ExplanatoryText("This content was present when the page first rendered at " + (new Date()).toString()));
        tabset1TabServer.setContent(new ExplanatoryText("This is the content of tab two. It should be noted that mode SERVER is deprecated.\nThis mode poses a number of usability problems and should not be used.\n This content was created at " + (new Date()).toString()));
        tabset1TabLazy.setContent(new ExplanatoryText("This tab content is rendered when the tab opens then remains static. Check the date stamp: " + (new Date()).toString()));
        tabset1TabDynamic.setContent(new ExplanatoryText("This tab content refreshes each time it is opened. Check the date stamp: " + (new Date()).toString()));
        tabset1TabEager.setContent(new ExplanatoryText("This tab content is fetched once asynchronously then remains static. Check the date stamp: " + (new Date()).toString()));
    }
    
    private static final class SampleTabSet extends WTabSet
    {
        public SampleTabSet()
        {
            super();
            init();
        }
        
        /**
         * rubbish shortcut to create a tabset with a particular tab open. This is just to show how to default to a tab 
         * other than the first.
         * 
         * @param activeIdx the tab index to set.
         */
        public SampleTabSet(int activeIdx)
        {
            this();
            WTab tab = getTab(activeIdx);
            if(tab != null)
            {
                setActiveIndex(activeIdx);
                getTab(activeIdx).setText("Active tab");
            }
        }
        
        private void init()
        {
            setMargin(new com.github.openborders.wcomponents.Margin(0, 0, 24, 0));
            addTab(new ExplanatoryText("Some content should go into here."), "First tab", WTabSet.TAB_MODE_CLIENT);
            addTab(new ExplanatoryText(LONG_TEXT), "Second tab", WTabSet.TAB_MODE_CLIENT);
            addTab(new ExplanatoryText("Some other content should go into here."), "Third tab", WTabSet.TAB_MODE_CLIENT);
        }
    }
}