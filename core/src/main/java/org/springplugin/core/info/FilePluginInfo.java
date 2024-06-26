package org.springplugin.core.info;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.exception.PluginException;
import org.springplugin.core.util.AssertUtils;
import org.springplugin.core.util.PluginExceptionUtils;
import org.springplugin.core.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 *  文件插件信息
 *
 * @author afěi
 * @version 1.0.0
 */

public class FilePluginInfo implements PluginInfo {

    private static final Gson GSON = new Gson();

    private final String name;
    private final String mainClassName;

    private FilePluginInfo(String name, String mainClassName) {
        AssertUtils.isTrue(StringUtils.isNotBlank(name), PluginExceptionUtils.pluginNameNotBlank());
        this.name = name;
        this.mainClassName = mainClassName;
    }

    /**
     * 创建文件插件信息
     *
     * @param name 插件名
     * @param mainClassName 主类名
     *
     */
    public static FilePluginInfo create(String name, String mainClassName) {
        final FilePluginInfo fif = new FilePluginInfo(name, mainClassName);
        final File pluginPath = new File(SpringPluginClassLoader.LOAD_PATH, name);
        final File infoFile = new File(pluginPath, ".info");
        if (!infoFile.exists()) {
            try {
                FileUtils.writeStringToFile(infoFile, GSON.toJson(fif), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new PluginException("Write file fail: '.info'", e);
            }
        }
        return fif;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Class<?> mainClass() throws ClassNotFoundException {
        return StringUtils.isNotBlank(this.mainClassName)
                ? SpringPluginClassLoader.getInstance(name()).forName(mainClassName())
                : PluginInfo.super.mainClass();
    }

    @Override
    public String mainClassName() {
        if (StringUtils.isNotBlank(this.mainClassName)) {
            return this.mainClassName;
        }
        final File pluginPath = new File(SpringPluginClassLoader.LOAD_PATH, name);
        final PluginException pe = PluginExceptionUtils.canNotFindPlugin(name);
        try {
            final File infoFile = new File(pluginPath, ".info");
            final String fileContent = FileUtils.readFileToString(infoFile, StandardCharsets.UTF_8);
            final FilePluginInfo fpi = Optional.ofNullable(GSON.fromJson(fileContent, this.getClass())).orElseThrow(() -> pe);
            return fpi.mainClassName;
        } catch (IOException e) {
            throw new PluginException(String.format("Can not find plugin '%s'", name), e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FilePluginInfo) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.mainClassName, that.mainClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mainClassName);
    }

    @Override
    public String toString() {
        return "FilePluginInfo[" +
                "name=" + name + ", " +
                "mainClassName=" + mainClassName + ']';
    }
}
