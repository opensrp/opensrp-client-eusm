package org.smartregister.eusm.helper;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.core.content.res.ResourcesCompat;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;

import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.ServicePointType;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.tasking.util.TaskingMapHelper;

import java.util.Map;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.linear;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class EusmTaskingMapHelper extends TaskingMapHelper {
    private boolean isSymbolLayersLoaded;

    @Override
    public void updateIndexCaseLayers(MapboxMap mapboxMap, FeatureCollection featureCollection, Context context) {
        super.updateIndexCaseLayers(mapboxMap, featureCollection, context);
        Style mMapboxMapStyle = mapboxMap.getStyle();

        // index case symbol layer
        if (!isSymbolLayersLoaded) {
            Expression dynamicIconSize = interpolate(linear(), zoom(),
                    literal(11.98f), literal(1),
                    literal(17.79f), literal(3f),
                    literal(18.8f), literal(4));
            Map<String, ServicePointType> servicePointTypeMap = EusmApplication.getInstance().getServicePointKeyToType();
            for (Map.Entry<String, ServicePointType> entry : servicePointTypeMap.entrySet()) {
                ServicePointType servicePointType = entry.getValue();
                String key = entry.getKey();
                Bitmap icon = AppUtils.drawableToBitmap(ResourcesCompat.getDrawable(context.getResources(), servicePointType.drawableId, context.getTheme()));
                mMapboxMapStyle.addImage(key, icon);
                SymbolLayer symbolLayer = new SymbolLayer(key, context.getString(R.string.reveal_datasource_name));
                symbolLayer.setProperties(iconImage(key),
                        iconIgnorePlacement(true), iconAllowOverlap(true));
                symbolLayer.setFilter(eq(get("type"), servicePointType.text));
                mMapboxMapStyle.addLayer(symbolLayer);

            }
        }
        isSymbolLayersLoaded = true;
    }


}
