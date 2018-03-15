package de.heisluft.capi.resources;

import java.io.InputStream;

/**
 * An AssetPack is a utility used to obtain {@link InputStream InputStreams} from
 * {@link ResourceLocation ResourceLocations}.
 *
 * @author Heisluft
 */
public interface IAssetPack {

	/**
	 * Establishes a connection to a specified, nonnull {@link ResourceLocation}.
	 * If the resource is not found, a {@link MissingResourceException} is thrown
	 *
	 * @param resource The resource to get
	 * @return A nonnull {@link InputStream} holding the resource content
	 * @throws MissingResourceException If the resource was not found
	 */
	InputStream getInputStream(ResourceLocation resource) throws MissingResourceException;

	/**
	 * Returns the name of this {@link IAssetPack AssetPack}.
	 * This name is used to draw information about the pack.
	 *
	 * @return The name of this AssetPack
	 */
	String getName();
}
