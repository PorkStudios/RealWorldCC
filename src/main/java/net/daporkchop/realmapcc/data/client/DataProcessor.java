package net.daporkchop.realmapcc.data.client;

import lombok.NonNull;
import net.daporkchop.lib.math.interpolation.CubicInterpolationEngine;
import net.daporkchop.lib.math.interpolation.InterpolationEngine;
import net.daporkchop.realmapcc.Constants;
import net.daporkchop.realmapcc.RealmapCC;
import net.daporkchop.realmapcc.data.client.lookup.CachedTileLookup;
import net.daporkchop.realmapcc.data.client.lookup.DiskTileCache;
import net.daporkchop.realmapcc.data.client.lookup.RepoTileLookup;
import net.daporkchop.realmapcc.data.client.lookup.TileLookup;
import net.minecraft.world.chunk.Chunk;

import java.io.File;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
public class DataProcessor implements Constants {
    protected final TileLookup tileLookup = new CachedTileLookup().setDelegate(new DiskTileCache(new File(RealmapCC.Conf.dataCacheDir)).setDelegate(new RepoTileLookup()));

    protected final InterpolationEngine engine = new CubicInterpolationEngine();
    protected final LookupGrid2d grid = new LookupGrid2d(this.tileLookup);

    public void prepare(@NonNull Chunk column, @NonNull short[] heights)    {
        int colX = column.x << 4;
        int colZ = column.z << 4;
        double lon = (colX / METERS_PER_ARCSECOND) * RealmapCC.Conf.scaleHoriz * ARCSECONDS_PER_DEGREE;
        double lat = (colZ / METERS_PER_ARCSECOND) * RealmapCC.Conf.scaleHoriz * ARCSECONDS_PER_DEGREE;
        for (int x = 15; x >= 0; x--)   {
            for (int z = 15; z >= 0; z--)   {
                heights[(x << 4) | z] = (short) floorI(this.engine.getInterpolated(lon + x * ARCSECONDS_PER_METER, lat + z * ARCSECONDS_PER_METER, this.grid));
            }
        }
    }
}
