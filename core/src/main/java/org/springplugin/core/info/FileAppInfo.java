package org.springplugin.core.info;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.springplugin.core.classloader.SpringAppClassLoader;
import org.springplugin.core.contant.PluginConstant;
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

public class FileAppInfo implements AppInfo {

    private static final Gson GSON = new Gson();

    private final String name;
    private final String mainClassName;

    private FileAppInfo(String name, String mainClassName) {
        AssertUtils.isTrue(StringUtils.isNotBlank(name), PluginExceptionUtils.pluginNameNotBlank());
        this.name = name;
        this.mainClassName = mainClassName;
    }

    /**
     * 创建文件插件信息
     *
     * @param name 插件名
     * @param mainClassName 主类名
     * @author afěi
     */
    public static FileAppInfo create(String name, String mainClassName) {
        final FileAppInfo fif = new FileAppInfo(name, mainClassName);
        final File pluginPath = new File(SpringAppClassLoader.LOAD_PATH, name);
        final File infoFile = new File(pluginPath, PluginConstant.INFO);
        if (!infoFile.exists()) {
            try {
                FileUtils.writeStringToFile(infoFile, GSON.toJson(fif), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new PluginException(String.format("Write file fail: '%s'", PluginConstant.INFO), e);
            }
        } else {
            return new FileAppInfo(name, fif.mainClassName());
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
                ? SpringAppClassLoader.getInstance(name()).forName(mainClassName())
                : AppInfo.super.mainClass();
    }

    @Override
    public String mainClassName() {
        if (StringUtils.isNotBlank(this.mainClassName)) {
            return this.mainClassName;
        }
        final File pluginPath = new File(SpringAppClassLoader.LOAD_PATH, name);
        final PluginException pe = PluginExceptionUtils.canNotFindPlugin(name);
        try {
            final File infoFile = new File(pluginPath, PluginConstant.INFO);
            final String fileContent = FileUtils.readFileToString(infoFile, StandardCharsets.UTF_8);
            final FileAppInfo fpi = Optional.ofNullable(GSON.fromJson(fileContent, this.getClass())).orElseThrow(() -> pe);
            return fpi.mainClassName;
        } catch (IOException e) {
            throw new PluginException(String.format("Can not find plugin info: '%s'", name), e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FileAppInfo) obj;
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
