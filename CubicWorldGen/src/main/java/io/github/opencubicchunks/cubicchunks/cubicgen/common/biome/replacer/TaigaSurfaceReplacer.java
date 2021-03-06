/*
 *  This file is part of Cubic World Generation, licensed under the MIT License (MIT).
 *
 *  Copyright (c) 2015 contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package io.github.opencubicchunks.cubicchunks.cubicgen.common.biome.replacer;

import io.github.opencubicchunks.cubicchunks.cubicgen.asm.mixin.common.accessor.IBiomeTaiga;
import io.github.opencubicchunks.cubicchunks.cubicgen.common.biome.CubicBiome;
import io.github.opencubicchunks.cubicchunks.cubicgen.common.biome.BiomeBlockReplacerConfig;
import io.github.opencubicchunks.cubicchunks.cubicgen.common.biome.ConfigOptionInfo;
import io.github.opencubicchunks.cubicchunks.cubicgen.common.biome.IBiomeBlockReplacer;
import io.github.opencubicchunks.cubicchunks.cubicgen.common.biome.IBiomeBlockReplacerProvider;
import io.github.opencubicchunks.cubicchunks.cubicgen.common.biome.SurfaceDefaultReplacer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeTaiga;

import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaigaSurfaceReplacer implements IBiomeBlockReplacer {

    public static final IBlockState COARSE_DIRT = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
    public static final IBlockState PODZOL = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
    private final SurfaceDefaultReplacer defaultReplacer;
    private final BiomeTaiga biome;

    public TaigaSurfaceReplacer(SurfaceDefaultReplacer defaultReplacer, BiomeTaiga biome) {
        this.defaultReplacer = defaultReplacer;
        this.biome = biome;
    }

    @Override public IBlockState getReplacedBlock(IBlockState previousBlock, int x, int y, int z, double dx, double dy, double dz, double density) {
        if (((IBiomeTaiga) biome).getType() == BiomeTaiga.Type.MEGA || ((IBiomeTaiga) biome).getType() == BiomeTaiga.Type.MEGA_SPRUCE) {
            defaultReplacer.setTopBlock(Blocks.GRASS.getDefaultState());
            defaultReplacer.setFillerBlock(Blocks.DIRT.getDefaultState());

            double depth = (defaultReplacer.getDepthNoise().get(x, 0, z) - 3) * 3;

            if (depth > 1.75D) {
                defaultReplacer.setTopBlock(COARSE_DIRT);
            } else if (depth > -0.95D) {
                defaultReplacer.setTopBlock(PODZOL);
            }
        }
        return defaultReplacer.getReplacedBlock(previousBlock, x, y, z, dx, dy, dz, density);
    }

    public static IBiomeBlockReplacerProvider provider() {
        return new IBiomeBlockReplacerProvider() {
            private final IBiomeBlockReplacerProvider parent = SurfaceDefaultReplacer.provider();

            @Override public IBiomeBlockReplacer create(World world, CubicBiome biome, BiomeBlockReplacerConfig conf) {
                return new TaigaSurfaceReplacer((SurfaceDefaultReplacer) parent.create(world, biome, conf), (BiomeTaiga) biome.getBiome());
            }

            @Override public Set<ConfigOptionInfo> getPossibleConfigOptions() {
                return parent.getPossibleConfigOptions();
            }
        };
    }
}
