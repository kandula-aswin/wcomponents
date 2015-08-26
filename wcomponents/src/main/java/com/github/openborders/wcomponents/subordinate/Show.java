package com.github.openborders.wcomponents.subordinate;

import com.github.openborders.wcomponents.SubordinateTarget;
import com.github.openborders.wcomponents.WLabel;

/**
 * An action that shows (makes visible) a given target component.
 * 
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Show extends AbstractSetVisible
{
    /**
     * Creates a show action with the given target.
     * 
     * @param target the component to show.
     */
    public Show(final SubordinateTarget target)
    {
        super(target, Boolean.TRUE);
    }

    /**
     * @return the action type of show.
     */
    public ActionType getActionType()
    {
        return ActionType.SHOW;
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        String targetName = getTarget().getClass().getSimpleName();

        WLabel label = getTarget().getLabel();
        if (label != null)
        {
            targetName = label.getText();
        }

        return "show " + targetName;
    }
    
}