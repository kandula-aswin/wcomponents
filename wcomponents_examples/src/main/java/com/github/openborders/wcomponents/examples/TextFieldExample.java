package com.github.openborders.wcomponents.examples;

import com.github.openborders.wcomponents.Action;
import com.github.openborders.wcomponents.ActionEvent;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.WButton;
import com.github.openborders.wcomponents.WDefinitionList;
import com.github.openborders.wcomponents.WFieldLayout;
import com.github.openborders.wcomponents.WHeading;
import com.github.openborders.wcomponents.WHorizontalRule;
import com.github.openborders.wcomponents.WLabel;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.WText;
import com.github.openborders.wcomponents.WTextField;
import com.github.openborders.wcomponents.layout.FlowLayout;
import com.github.openborders.wcomponents.layout.FlowLayout.Alignment;
import com.github.openborders.wcomponents.validation.ValidatingAction;
import com.github.openborders.wcomponents.validation.WValidationErrors;

/**
 * <p>
 * A {@link WTextField} is a wcomponent used to display a html text input field.
 * </p>
 * <p>
 * WTextField example demonstrates various states of the WTextField
 * </p>
 * <p>
 * including
 * <ul>
 * <li>Default</li>
 * <li>Size limited</li>
 * <li>Read Only</li>
 * <li>Disabled</li>
 * </ul>
 * </p>
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 */
public class TextFieldExample extends WPanel
{
    /** Text field 1. */
    private final WTextField tf1;
    /** Text field 2. */
    private final WTextField tf2;
    /** Text field 3. */
    private final WTextField tf3;
    /** Text field 4. */
    private final WTextField tf4;
    /** Text field 5. */
    private final WTextField tf5;

    /** holds the plain results. */
    private final WText plain = new WText();
    /** holds the mandatory results. */
    private final WText mandatory = new WText();
    /** holds the readOnly results. */
    private final WText readOnly = new WText();
    /** holds the disabled results. */
    private final WText disabled = new WText();
    /** holds the width results. */
    private final WText width = new WText();

    /**
     * Creates a TextFieldExample.
     */
    public TextFieldExample()
    {
        setLayout(new FlowLayout(Alignment.VERTICAL));

        WValidationErrors errors = new WValidationErrors();
        add(errors);

        WFieldLayout fieldLayout = new WFieldLayout();
        tf1 = new WTextField();
        tf1.setMaxLength(6);
        fieldLayout.addField("Plain maxlength 6", tf1);
        add(fieldLayout);
        
        tf2 = new WTextField();
        tf2.setMandatory(true);
        fieldLayout.addField("Mandatory", tf2);
        
        tf3 = new WTextField();
        tf3.setReadOnly(true);
        fieldLayout.addField("Read only", tf3);
        
        tf4 = new WTextField();
        tf4.setDisabled(true);
        fieldLayout.addField("Disabled", tf4);

        WButton toggleDisableButton = new WButton("Toggle disable");
        toggleDisableButton.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                tf4.setDisabled(!tf4.isDisabled());
            }
        });
        toggleDisableButton.setAjaxTarget(tf4);

        fieldLayout.addField((WLabel) null, toggleDisableButton);

        tf5 = new WTextField();
        tf5.setColumns(10);
        fieldLayout.addField("Ten column field", tf5);

        add(new WHorizontalRule());
        WHeading heading = new WHeading(WHeading.MAJOR, "Values read from fields");
        add(heading);
        WDefinitionList defList = new WDefinitionList(WDefinitionList.Type.COLUMN);
        defList.addTerm(tf1.getLabel().getText(), plain);
        defList.addTerm(tf2.getLabel().getText(), mandatory);
        defList.addTerm(tf3.getLabel().getText(), readOnly);
        defList.addTerm(tf4.getLabel().getText(), disabled);
        defList.addTerm(tf5.getLabel().getText(), width);
        add(defList);

        WButton readFieldsButton = new WButton("Read Fields");
        readFieldsButton.setAction(new ValidatingAction(errors, this)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                readFields();
            }
        });
        
        add(readFieldsButton);
    }

    /**
     * Override preparePaintComponent to test that dynamic attributes are handled correctly.
     * 
     * @param request the request that triggered the paint.
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);

        if (!isInitialised())
        {
            tf3.setText("This is read only.");
            tf4.setText("This is disabled.");
            readFields();
            setInitialised(true);
        }
    }

    /**
     * Read fields is a simple method to read all the fields and populate the
     * encoded text fields. 
     */
    private void readFields()
    {
        plain.setText(tf1.getText());
        mandatory.setText(tf2.getText());
        readOnly.setText(tf3.getText());
        disabled.setText(tf4.getText());
        width.setText(tf5.getText());
    }
}