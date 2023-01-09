package com.isaac.tutorialmod.item.client;

import com.isaac.tutorialmod.item.custom.ToydogFigureItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class ToydogFigureItemRenderer extends GeoItemRenderer<ToydogFigureItem> {
    public ToydogFigureItemRenderer() { super(new ToydogFigureItemModel());}
}
