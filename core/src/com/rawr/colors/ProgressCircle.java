package com.rawr.colors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ShortArray;

public class ProgressCircle extends Image {

    public enum IntersectAt {
        NONE, TOP, BOTTOM, LEFT, RIGHT
    }

    TextureRegion texture;

    PolygonSpriteBatch polyBatch;

    Vector2 center;
    Vector2 centerTop;
    Vector2 leftTop;
    Vector2 leftBottom;
    Vector2 rightBottom;
    Vector2 rightTop;
    Vector2 progressPoint;

    float[] fv;

    IntersectAt intersectAt;

    float real_w;
    float real_h;

    public ProgressCircle(TextureRegion region, PolygonSpriteBatch polyBatch, float width, float height) {
        super(region);

        this.texture = region;
        this.polyBatch = polyBatch;

        real_w = this.getWidth();
        real_h = this.getHeight();
        this.setWidth(width);
        this.setHeight(height);

        calcVectors();

        setPercentage(0);
    }

    public void calcVectors(){
        center = new Vector2(real_w / 2, real_h / 2);
        centerTop = new Vector2(real_w / 2, real_h);
        leftTop = new Vector2(0, real_w);
        leftBottom = new Vector2(0, 0);
        rightBottom = new Vector2(real_w, 0);
        rightTop = new Vector2(real_w, real_h);
        progressPoint = new Vector2(real_w / 2, real_h / 2);
    }

    private Vector2 IntersectPoint(Vector2 line) {
        Vector2 v = new Vector2();
        boolean isIntersect;

        //check top
        isIntersect = Intersector.intersectSegments(leftTop, rightTop, center, line, v);

        //check bottom
        if (isIntersect) {
            intersectAt = IntersectAt.TOP;
            return v;
        } else
            isIntersect = Intersector.intersectSegments(leftBottom, rightBottom, center, line, v);

        //check left
        if (isIntersect) {
            intersectAt = IntersectAt.BOTTOM;
            return v;
        } else isIntersect = Intersector.intersectSegments(leftTop, leftBottom, center, line, v);

        //check bottom
        if (isIntersect) {
            intersectAt = IntersectAt.LEFT;
            return v;
        } else isIntersect = Intersector.intersectSegments(rightTop, rightBottom, center, line, v);

        if (isIntersect) {
            intersectAt = IntersectAt.RIGHT;
            return v;
        } else {
            intersectAt = IntersectAt.NONE;
            return null;
        }
    }

    public void setPercentage(float percent) {
        //100 % = 360 degree
        //==> percent % => (percent * 360 / 100) degree

        float angle = convertToRadians(90); //percent = 0 => angle = -90
        angle -= convertToRadians(percent * 360 / 100);

        float len = real_w > real_h ? real_w : real_h;
        float dy = (float) (Math.sin(angle) * len);
        float dx = (float) (Math.cos(angle) * len);
        Vector2 line = new Vector2(center.x + dx, center.y + dy);

        Vector2 v = IntersectPoint(line);

        if (intersectAt == IntersectAt.TOP) {
            if (v.x >= real_w / 2)
            {
                fv = new float[]{
                        center.x,
                        center.y,
                        centerTop.x,
                        centerTop.y,
                        leftTop.x,
                        leftTop.y,
                        leftBottom.x,
                        leftBottom.y,
                        rightBottom.x,
                        rightBottom.y,
                        rightTop.x,
                        rightTop.y,
                        v.x,
                        v.y
                };
            } else {
                fv = new float[]{
                        center.x,
                        center.y,
                        centerTop.x,
                        centerTop.y,
                        v.x,
                        v.y
                };

            }
        } else if (intersectAt == IntersectAt.BOTTOM) {
            fv = new float[]{
                    center.x,
                    center.y,
                    centerTop.x,
                    centerTop.y,
                    leftTop.x,
                    leftTop.y,
                    leftBottom.x,
                    leftBottom.y,
                    v.x,
                    v.y
            };

        } else if (intersectAt == IntersectAt.LEFT) {
            fv = new float[]{
                    center.x,
                    center.y,
                    centerTop.x,
                    centerTop.y,
                    leftTop.x,
                    leftTop.y,
                    v.x,
                    v.y
            };

        } else if (intersectAt == IntersectAt.RIGHT) {
            fv = new float[]{
                    center.x,
                    center.y,
                    centerTop.x,
                    centerTop.y,
                    leftTop.x,
                    leftTop.y,
                    leftBottom.x,
                    leftBottom.y,
                    rightBottom.x,
                    rightBottom.y,
                    v.x,
                    v.y
            };

        } else // if (intersectAt == IntersectAt.NONE)
        {
            fv = null;
        }


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);

        if (fv == null) return;

        batch.end();
        drawMe();
        batch.begin();

    }

    public void drawMe() {
        EarClippingTriangulator e = new EarClippingTriangulator();
        ShortArray sv = e.computeTriangles(fv);

        PolygonRegion polyReg = new PolygonRegion(texture, fv, sv.toArray());

        PolygonSprite poly = new PolygonSprite(polyReg);

        poly.setOrigin(this.getOriginX(), this.getOriginY());
        poly.setPosition(this.getX(), this.getY());
        poly.setRotation(this.getRotation());
        poly.setColor(this.getColor());

        poly.setSize(this.getWidth(), this.getHeight());

        polyBatch.begin();
        poly.draw(polyBatch);
        polyBatch.end();

    }

/*-----------------------------------------------------------------*/

    float convertToDegrees(float angleInRadians) {
        return angleInRadians * 57.2957795f;
    }

    float convertToRadians(float angleInDegrees) {
        return angleInDegrees * 0.0174532925f;
    }
}
