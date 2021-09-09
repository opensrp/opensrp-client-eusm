package org.smartregister.eusm.layer;

import android.content.Context;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.RasterSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.style.sources.VectorSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import io.ona.kujaku.plugin.switcher.ExpressionArrayLiteral;
import io.ona.kujaku.plugin.switcher.layer.BaseLayer;
import io.ona.kujaku.utils.IOUtil;
import io.ona.kujaku.utils.LayerUtil;
import timber.log.Timber;

public class SatelliteStreetsLayer extends BaseLayer {

    private String streetSourceId = "composite";
    private ArrayList<Source> sourcesList = new ArrayList<>();
    private LinkedHashSet<Layer> layers = new LinkedHashSet<>();
    private String satelliteSourceId = "mapbox://mapbox.satellite";

    public SatelliteStreetsLayer(@NonNull Context context) {
        createLayersAndSources(context);
    }

    protected void createLayersAndSources(@NonNull Context context) {
        RasterSource rasterSource = new RasterSource(satelliteSourceId, satelliteSourceId, 256);
        VectorSource streetSource = new VectorSource(streetSourceId, "mapbox://mapbox.mapbox-streets-v8");
        sourcesList.add(streetSource);
        sourcesList.add(rasterSource);

        LayerUtil layerUtil = new LayerUtil();

        try {
            JSONArray jsonArray = new JSONArray(
                    IOUtil.readInputStreamAsString(context.getAssets().open("satellite_streets_style.json"))
            );

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Layer layer = layerUtil.getLayer(jsonObject.toString());

                if (layer != null && "hillshade".equals(layer.getId()) && layer instanceof FillLayer) {
                    // Add the correct opacity
                    Expression fillOpacityExpression = Expression.interpolate(Expression.Interpolator.linear()
                            , Expression.zoom()
                            , Expression.literal(14)
                            , Expression.match(
                                    Expression.get("level"), Expression.literal(0.12),
                                    Expression.stop(new ExpressionArrayLiteral(new Object[]{67, 56}), 0.06),
                                    Expression.stop(new ExpressionArrayLiteral(new Object[]{89, 78}), 0.05)
                            ), Expression.literal(16), Expression.literal(0));

                    ((FillLayer) layer).withProperties(PropertyFactory.fillOpacity(fillOpacityExpression));
                }

                if (layer != null) {
                    layers.add(layer);
                }
            }
        } catch (IOException | JSONException e) {
            Timber.e(e);
        }
    }

    @NonNull
    @Override
    public String getDisplayName() {
        return "Satellite + Streets";
    }

    @NonNull
    @Override
    public String[] getSourceIds() {
        return new String[]{streetSourceId};
    }

    @Override
    public LinkedHashSet<Layer> getLayers() {
        return layers;
    }

    @Override
    public List<Source> getSources() {
        return sourcesList;
    }

    @NonNull
    @Override
    public String getId() {
        return "satellite-street-base-layer";
    }

    @NonNull
    @Override
    public String[] getLayerIds() {
        String[] layerIds = new String[layers.size()];

        int counter = 0;
        for (Layer layer : layers) {
            layerIds[counter] = layer.getId();
            counter++;
        }

        return layerIds;
    }
}
