/*
 * Copyright (c) 2014 μg Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.microg.gms.maps.markup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.R;
import com.google.android.gms.maps.internal.IOnInfoWindowClickListener;
import com.google.android.gms.maps.model.internal.IMarkerDelegate;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import org.microg.gms.maps.GoogleMapImpl;
import org.microg.gms.maps.ResourcesContainer;

public class InfoWindow extends Overlay {
    private static final String TAG = InfoWindow.class.getName();
    private Context context;
    private View window;
    private GoogleMapImpl map;
    private MarkerImpl marker;

    public InfoWindow(Context context, final GoogleMapImpl map, final MarkerImpl marker) {
        super();
        this.context = context;
        this.map = map;
        this.marker = marker;
    }

    public void setWindow(View view) {
        window = view;
        if (window != null) {
            window.measure(0, 0);
        }
    }

    public boolean isComplete() {
        return window != null;
    }

    public void setContent(View view) {
        if (view == null) return;
        setWindow(new DefaultWindow(view));
    }

    public void buildDefault() {
        try {
            if (marker.getTitle() != null)
            setContent(new DefaultContent());
        } catch (RemoteException e) {
            // Not remote...
        }
    }

    public void destroy() {
        if (window instanceof DefaultWindow) {
            ((DefaultWindow) window).removeAllViews();
        }
    }

    public IMarkerDelegate getMarker() {
        return marker;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (window != null && marker.getHeight() != -1 && !shadow) {
            try {
                Log.d(TAG, "draw InfoWindow");
                window.measure(0, 0);
                window.layout(0, 0, window.getMeasuredWidth(), window.getMeasuredHeight());
                Point point = mapView.getProjection().toPixels(marker.getPosition().toGeoPoint(), null);
                /*
                // osmdroid 4.1 bugfix
                Point zero = mapView.getProjection().toPixels(new GeoPoint(0, 0), null);
                point.offset(-zero.x, -zero.y);
                */

                point.offset(-window.getMeasuredWidth() / 2, -window.getMeasuredHeight() - marker.getHeight());
                Log.d(TAG, point.toString());
                canvas.save();
                canvas.translate(point.x, point.y);
                window.draw(canvas);
                canvas.restore();
            } catch (RemoteException e) {
                // This is not remote...
            }
        }
    }

    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        try {
            IOnInfoWindowClickListener listener = map.getInfoWindowClickListener();
            if (listener != null) {
                Point clickPoint = mapView.getProjection().toPixels(p, null);
                Point markerPoint = mapView.getProjection().toPixels(marker.getPosition().toGeoPoint(), null);
                Rect rect = new Rect(markerPoint.x - (window.getMeasuredWidth() / 2),
                        markerPoint.y - marker.getHeight() - window.getMeasuredHeight(),
                        markerPoint.x + (window.getMeasuredWidth() / 2),
                        markerPoint.y - marker.getHeight());
                if (rect.contains(clickPoint.x, clickPoint.y)) {
                    try {
                        listener.onInfoWindowClick(marker);
                    } catch (RemoteException e) {
                        Log.w(TAG, e);
                    }
                    return true;
                }
            }
        } catch (RemoteException e) {
            // This is not remote...
        }
        return false;
    }

    private class DefaultWindow extends FrameLayout {
        public DefaultWindow(View view) {
            super(context);
            addView(view);
            setBackground(ResourcesContainer.get().getDrawable(R.drawable.maps_default_window));
        }
    }

    private class DefaultContent extends LinearLayout {
        public DefaultContent() {
            super(context);
            setOrientation(LinearLayout.VERTICAL);
            try {
                TextView title = new TextView(context);
                title.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Medium_Inverse);
                title.setText(marker.getTitle());
                addView(title);
                if (marker.getSnippet() != null) {
                    TextView snippet = new TextView(context);
                    snippet.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Inverse);
                    snippet.setText(marker.getSnippet());
                    addView(snippet);
                }
            } catch (RemoteException e) {
                // ...
            }
        }
    }
}
