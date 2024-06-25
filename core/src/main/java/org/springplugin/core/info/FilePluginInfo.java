package org.springplugin.core.info;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.exception.PluginException;
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

    private final String name;
    private final String mainClassName;
    private static final Gson GSON = new Gson();

    private FilePluginInfo(String name, String mainClassName) {
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
    public static PluginInfo create(String name, String mainClassName) {
        final FilePluginInfo fif = new FilePluginInfo(name, mainClassName);
        if (StringUtils.isNotBlank(mainClassName)) {
            final File pluginPath = new File(SpringPluginClassLoader.LOAD_PATH, name);
            final File infoFile = new File(pluginPath, ".info");
            if (!infoFile.exists()) {
                try {
                    FileUtils.writeStringToFile(infoFile, GSON.toJson(fif), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new PluginException("Write file fail: '.info'", e);
                }
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
        if (Objects.isNull(this.name)) {
            return null;
        }
        final File pluginPath = new File(SpringPluginClassLoader.LOAD_PATH, name);
        final PluginException pe = new PluginException(String.format("Can not find plugin '%s'", name));
        try {
            final File infoFile = new File(pluginPath, ".info");
            if (infoFile.exists()) {
                final String fileContent = FileUtils.readFileToString(new File(pluginPath, ".info"), StandardCharsets.UTF_8);
                final FilePluginInfo fif = Optional.ofNullable(GSON.fromJson(fileContent, this.getClass())).orElseThrow(() -> pe);
                return fif.mainClassName;
            }
        } catch (IOException e) {
            throw new PluginException(String.format("Can not find plugin '%s'", name), e);
        }
        return null;
    }
}
