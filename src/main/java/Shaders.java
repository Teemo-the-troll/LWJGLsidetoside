import org.lwjgl.opengl.GL33;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Shaders {
    private static String readFile(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            StringBuilder content = new StringBuilder();

            while (line != null) {
                content.append(line).append("\n");
                line = br.readLine();
            }

            br.close();
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static final String vertexShaderSource = readFile("src/main/resources/vertex-shader.glsl");

    private static final String fragmentShaderSource = readFile("src/main/resources/fragment-shader.glsl");

    public static int vertexShaderId;
    public static int fragmentShaderId;
    public static int shaderProgramId;

    public static void initShaders() {
        // Generate the shader ids
        vertexShaderId = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
        fragmentShaderId = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);

        //region: VertexShader
        // Compile the vertexShader
        GL33.glShaderSource(vertexShaderId, vertexShaderSource);
        GL33.glCompileShader(vertexShaderId);

        // Print the log... TODO: Check for errors
        System.out.println(GL33.glGetShaderInfoLog(vertexShaderId));
        //endregion

        //region: FragmentShader
        // Compile the fragmentShader
        GL33.glShaderSource(fragmentShaderId, fragmentShaderSource);
        GL33.glCompileShader(fragmentShaderId);

        // Print the log... TODO: Check for errors
        System.out.println(GL33.glGetShaderInfoLog(fragmentShaderId));
        //endregion

        //region: Shader attachment
        shaderProgramId = GL33.glCreateProgram();
        GL33.glAttachShader(shaderProgramId, vertexShaderId);
        GL33.glAttachShader(shaderProgramId, fragmentShaderId);
        GL33.glLinkProgram(shaderProgramId);

        System.out.println(GL33.glGetProgramInfoLog(shaderProgramId));
        //endregion

        // We don't need them anymore... It's saved on the GPU now
        GL33.glDeleteShader(vertexShaderId);
        GL33.glDeleteShader(fragmentShaderId);
    }


}
