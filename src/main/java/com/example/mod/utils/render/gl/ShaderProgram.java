package com.example.mod.utils.render.gl;

import com.example.utils.FileUtils;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.File;
import java.io.IOException;

public class ShaderProgram {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public ShaderProgram() {
        programId = GL20.glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader");
        }
    }

    public void addShader(ShaderType type, String filePath) throws IOException {
        int shaderId;
        switch (type) {
            case VERTEX:
                shaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
                break;
            case FRAGMENT:
                shaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
                break;
            default:
                throw new IllegalArgumentException("Unsupported shader type");
        }

        // 读取着色器代码并去除多余的换行符
        String shaderSource = new String(FileUtils.readFile(new File(filePath).toPath().toFile()));
        GL20.glShaderSource(shaderId, shaderSource);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + GL20.glGetShaderInfoLog(shaderId));
        }

        GL20.glAttachShader(programId, shaderId);
        if (type == ShaderType.VERTEX) {
            vertexShaderId = shaderId;
        } else {
            fragmentShaderId = shaderId;
        }
    }

    public void compile() {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Program linking failed: " + GL20.glGetProgramInfoLog(programId));
        }

        // Validate program
        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId));
        }
    }

    public void use() {
        GL20.glUseProgram(programId);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void setMouseUniform(float mouseX, float mouseY) {
        int location = GL20.glGetUniformLocation(programId, "mouse");
        if (location != -1) {
            GL20.glUniform2f(location, mouseX, mouseY);
        } else {
            System.err.println("Uniform 'mouse' not found.");
        }
    }


    public void setUniform(String name, Vector2f value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform2f(location, value.x, value.y);
        } else {
            System.err.println("Uniform '" + name + "' not found.");
        }
    }

    public void setUniform(String name, float value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform1f(location, value);
        } else {
            System.err.println("Uniform '" + name + "' not found.");
        }
    }

    public void cleanup() {
        stop();
        if (programId != 0) {
            if (vertexShaderId != 0) {
                GL20.glDetachShader(programId, vertexShaderId);
                GL20.glDeleteShader(vertexShaderId);
            }
            if (fragmentShaderId != 0) {
                GL20.glDetachShader(programId, fragmentShaderId);
                GL20.glDeleteShader(fragmentShaderId);
            }
            GL20.glDeleteProgram(programId);
            programId = 0;
        }
    }
}
