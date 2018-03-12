package de.heisluft.capi.resources;

/**
 * Manages Resources of the game
 *
 * @author Heisluft
 */
public interface IAssetManager {

	/**
	 * Gets the current {@link IAssetPack}
	 * @return the current asset pack
	 */
	IAssetPack getCurrentAssets();
}
