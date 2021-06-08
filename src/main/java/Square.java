import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Square {
    private final int[] indices = {
            0, 1, 3,
            1, 2, 3
    };

    private final float[] textures = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };

    private final int vaoId;
    private final int vboId;
    private final int eboId;
    private final int texId;
    private final int texIndId;
    private final FloatBuffer matFloatBuff;
    private final float sideSize;
    private final float[] verticies;
    private int uniMatrixLoc;
    private float x;
    private float y;
    private Matrix4f matrix = new Matrix4f().translation(0f, 0f, 0f);

    public Square(float x, float y, float sideSize) {
        this.vaoId = GL33.glGenVertexArrays();
        this.vboId = GL33.glGenBuffers();
        this.eboId = GL33.glGenBuffers();
        this.texId = GL33.glGenTextures();
        this.texIndId = GL33.glGenBuffers();
        this.sideSize = sideSize;
        this.matFloatBuff = BufferUtils.createFloatBuffer(16);
        this.x = x;
        this.y = y;
        this.verticies = new float[]{
                sideSize, sideSize, 0.0f, // 0 -> Top right
                sideSize, -sideSize, 0.0f, // 1 -> Bottom right
                -sideSize, -sideSize, 0.0f, // 2 -> Bottom left
                -sideSize, sideSize, 0.0f, // 3 -> Top left
        };
    }

    public void render() {
        GL33.glBindVertexArray(this.vaoId);
        GL33.glDrawElements(GL33.GL_TRIANGLES, indices.length, GL33.GL_UNSIGNED_INT, 0);
    }

    public void update(float x, float y) {
        this.x += x;
        this.y += y;
        matrix = matrix.translate(x, y, 0f);

        matrix.get(matFloatBuff);
        GL33.glUniformMatrix4fv(uniMatrixLoc, false, matFloatBuff);
    }

    public Side colides() {
        if (this.x - sideSize < -1f)
            return Side.LEFT;
        if (this.x + sideSize > 1f)
            return Side.RIGHT;
        if (this.y - sideSize < -1f)
            return Side.BOTTOM;
        if (this.y + sideSize > 1f)
            return Side.TOP;
        return null;
    }

    public void init() {
        Shaders.initShaders();
        loadImg();

        uniMatrixLoc = GL33.glGetUniformLocation(Shaders.shaderProgramId, "matrix");

        GL33.glBindVertexArray(this.vaoId);

        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, this.eboId);
        IntBuffer ib = BufferUtils.createIntBuffer(this.indices.length)
                .put(this.indices)
                .flip();
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, this.vboId);
        FloatBuffer fb = BufferUtils.createFloatBuffer(this.verticies.length)
                .put(verticies)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);
        MemoryUtil.memFree(fb);


        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, texIndId);
        FloatBuffer tb = BufferUtils.createFloatBuffer(textures.length)
                .put(textures)
                .flip();

        // Send the buffer (positions) to the GPU
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, tb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(1);

        GL33.glUseProgram(Shaders.shaderProgramId);

        matrix.get(matFloatBuff);
        GL33.glUniformMatrix4fv(uniMatrixLoc, false, matFloatBuff);

        MemoryUtil.memFree(tb);
    }

    private void loadImg() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer img = STBImage.stbi_load("src/main/resources/img.png", w, h, comp, 3);
            if (img != null) {
                img.flip();

                GL33.glBindTexture(GL33.GL_TEXTURE_2D, texId);
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, w.get(), h.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, img);
                GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

                STBImage.stbi_image_free(img);
            }
        }
    }
}

