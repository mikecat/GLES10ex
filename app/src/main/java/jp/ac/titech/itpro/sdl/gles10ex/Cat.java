package jp.ac.titech.itpro.sdl.gles10ex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cat implements SimpleRenderer.Obj {
    private final static float[][] positions = {
            {0.0f, 1.0f / 2.0f},
            {1.0f / 3.0f, 1.0f / 2.0f},
            {2.0f / 3.0f, 1.0f},
            {1.0f, 1.0f / 4.0f},
            {1.0f, -1.0f / 2.0f},
            {1.0f / 2.0f, -1.0f},
            {-1.0f / 2.0f, -1.0f},
            {-1.0f, -1.0f / 2.0f},
            {-1.0f, 1.0f / 4.0f},
            {-2.0f / 3.0f, 1.0f},
            {-1.0f / 3.0f, 1.0f / 2.0f}
    };
    private final static short[] frontIndex = {
            10, 9, 8, 7, 0,
            0, 7, 6, 5, 4,
            1, 0, 4, 3, 2
    };
    private final static short[] backIndex = {
            10 + 11, 0 + 11, 7 + 11, 8 + 11, 9 + 11,
            0 + 11, 4 + 11, 5 + 11, 6 + 11, 7 + 11,
            1 + 11, 2 + 11, 3 + 11, 4 + 11, 0 + 11
    };
    private final static int[][] sideRange = {
            {7, 11},
            {4, 7},
            {0, 4}
    };
    private final static float[][] colors = {
            {0.3f, 0.3f, 0.3f},
            {1.0f, 1.0f, 1.0f},
            {0.7f, 0.4f, 0.4f}
    };
    private float x, y, z;
    private FloatBuffer vertexBuffer;
    private ShortBuffer frontIndexBuffer, sideIndexBuffer, backIndexBuffer;
    private float[][] positionsNorms;

    public Cat(float s, float x, float y, float z) {
        float[] vertex = new float[positions.length * 2 * 3];
        for (int i = 0; i < positions.length; i++) {
            vertex[i * 3 + 0] = vertex[(positions.length + i) * 3 + 0] = s * positions[i][0];
            vertex[i * 3 + 1] = vertex[(positions.length + i) * 3 + 1] = s * positions[i][1];
            vertex[i * 3 + 2] = s * 0.5f;
            vertex[(positions.length + i) * 3 + 2] = -s * 0.5f;
        }
        vertexBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertex);
        vertexBuffer.position(0);

        frontIndexBuffer = ByteBuffer.allocateDirect(frontIndex.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        frontIndexBuffer.put(frontIndex);

        backIndexBuffer = ByteBuffer.allocateDirect(backIndex.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        backIndexBuffer.put(backIndex);

        short[] sideIndex = new short[positions.length * 2 + 2];
        for (int i = 0; i < positions.length; i++) {
            sideIndex[i * 2 + 0] = (short)(i + positions.length);
            sideIndex[i * 2 + 1] = (short)i;
        }
        sideIndex[positions.length * 2 + 0] = sideIndex[0];
        sideIndex[positions.length * 2 + 1] = sideIndex[1];
        sideIndexBuffer = ByteBuffer.allocateDirect(sideIndex.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        sideIndexBuffer.put(sideIndex);
        positionsNorms = new float[positions.length][2];
        for (int i = 0; i < positions.length; i++) {
            float[] from = positions[i];
            float[] to = positions[(i + 1) % positions.length];
            float length = (float)Math.sqrt((to[0] - from[0]) * (to[0] - from[0]) +
                    (to[1] - from[1]) * (to[1] - from[1]));
            positionsNorms[i][0] = -(to[1] - from[1]) / length;
            positionsNorms[i][1] = (to[0] - from[0]) / length;
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        for (int i = 0; i < 3; i++) {
            gl.glColor4f(colors[i][0], colors[i][1], colors[i][2], 1.0f);
            gl.glNormal3f(0, 0, 1);
            frontIndexBuffer.position(i * 5);
            gl.glDrawElements(GL10.GL_TRIANGLE_FAN, 5, GL10.GL_UNSIGNED_SHORT, frontIndexBuffer);
            gl.glNormal3f(0, 0, -1);
            backIndexBuffer.position(i * 5);
            gl.glDrawElements(GL10.GL_TRIANGLE_FAN, 5, GL10.GL_UNSIGNED_SHORT, backIndexBuffer);
            for (int j = sideRange[i][0]; j < sideRange[i][1]; j++) {
                gl.glNormal3f(positionsNorms[j][0], positionsNorms[j][1], 0);
                sideIndexBuffer.position(j * 2);
                gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_SHORT, sideIndexBuffer);
            }
        }

        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glDisable(GL10.GL_COLOR_MATERIAL);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }
}
