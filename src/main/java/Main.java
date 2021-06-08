import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

public class Main {
    public static void main(String[] args) throws Exception {
        long window = Window.windowCreate();
        float xMovement = 0.006f; //this was carefully crafted so it resembles the dvd screensaver
        Side collidingSide;
        Square dvd = new Square(0, 0, 0.3f);
        dvd.init();

        while (!GLFW.glfwWindowShouldClose(window)) {
            GL33.glClearColor(0f, 0f, 0f, 1f);
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);

            dvd.render();
            dvd.update(xMovement, 0);
            collidingSide = dvd.colides();
            if (collidingSide != null){
                switch (collidingSide){
                    case LEFT, RIGHT -> xMovement *= -1f;
                    default -> {}
                }
            }
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
        GLFW.glfwTerminate();
    }
}
