/*|~^~|Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 |~^~|All rights reserved.
 |~^~|
 |~^~|Redistribution and use in source and binary forms, with or without
 |~^~|modification, are permitted provided that the following conditions are met:
 |~^~|
 |~^~|-1. Redistributions of source code must retain the above copyright notice, this
 |~^~|ist of conditions and the following disclaimer.
 |~^~|
 |~^~|-2. Redistributions in binary form must reproduce the above copyright notice,
 |~^~|this list of conditions and the following disclaimer in the documentation
 |~^~|and/or other materials provided with the distribution.
 |~^~|
 |~^~|-3. Neither the name of the copyright holder nor the names of its contributors
 |~^~|may be used to endorse or promote products derived from this software without
 |~^~|specific prior written permission.
 |~^~|
 |~^~|THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 |~^~|AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 |~^~|IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 |~^~|DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 |~^~|FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 |~^~|DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 |~^~|SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 |~^~|CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 |~^~|OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 |~^~|OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*/
/**
 *
 */
package scout.edu.mit.ll.nics.android.maps.markup.tileprovider;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

public abstract class MarkupCanvasTileProvider implements TileProvider {
	private static int TILE_SIZE = 512;

	private BitmapThreadLocal tlBitmap;

	public MarkupCanvasTileProvider() {
		super();
		tlBitmap = new BitmapThreadLocal();
	}

	@Override
	public Tile getTile(int x, int y, int zoom) {
		Tile tile = null;
		try {
			MarkupTileProjection projection = new MarkupTileProjection(TILE_SIZE, x, y, zoom);

			byte[] data;
			Bitmap image = getNewBitmap();
			Canvas canvas = new Canvas(image);
			onDraw(canvas, projection);
			data = bitmapToByteArray(image);
			tile = new Tile(TILE_SIZE, TILE_SIZE, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tile;
	}

	/** Must be implemented by a concrete TileProvider */
	abstract void onDraw(Canvas canvas, MarkupTileProjection projection);

	/**
	 * Get an empty bitmap, which may however be reused from a previous call in
	 * the same thread.
	 * 
	 * @return
	 */
	private Bitmap getNewBitmap() {
		Bitmap bitmap = tlBitmap.get();
		// Clear the previous bitmap
		bitmap.eraseColor(Color.TRANSPARENT);
		return bitmap;
	}

	private static byte[] bitmapToByteArray(Bitmap bm) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
		byte[] data = bos.toByteArray();
		return data;
	}

	class BitmapThreadLocal extends ThreadLocal<Bitmap> {
		@Override
		protected Bitmap initialValue() {
			Bitmap image = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Config.ARGB_4444);
			return image;
		}
	}
}
