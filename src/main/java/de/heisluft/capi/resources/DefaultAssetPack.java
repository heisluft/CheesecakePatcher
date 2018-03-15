package de.heisluft.capi.resources;

import java.io.InputStream;

/**
 * The default asset pack
 *
 * @author Heisluft
 */
public class DefaultAssetPack implements IAssetPack {

	/** The classloader used to find resources */
	private static final ClassLoader CLASS_LOADER = DefaultAssetPack.class.getClassLoader();

	@Override
	public String getName() {
		return "cheesecake default";
	}

	@Override
	public InputStream getInputStream(ResourceLocation resource) throws MissingResourceException{
		String name = "assets/" + resource.getResourceDomain() + "/" + resource.getLocation();
		InputStream inputStream = CLASS_LOADER.getResourceAsStream(name);
		if(inputStream == null) throw new MissingResourceException(resource);
		return inputStream;
	}
}
