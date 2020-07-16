package ladysnake.nomadbooks.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class CampLimitParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    private CampLimitParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.gravityStrength = 0.0F;
        this.maxAge = 9;
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider);
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    public float getSize(float tickDelta) {
        return 0.05f + (float) Math.sin(world.getTime()/10f)/50f;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public DefaultFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new CampLimitParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternion quaternion2;
        if (this.angle == 0.0F) {
            quaternion2 = camera.getRotation();
        } else {
            quaternion2 = new Quaternion(camera.getRotation());
            float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
            quaternion2.hamiltonProduct(Vector3f.POSITIVE_Z.getRadialQuaternion(i));
        }

        Vector3f vector3f = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f.rotate(quaternion2);
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float j = this.getSize(tickDelta);

        for(int k = 0; k < 4; ++k) {
            Vector3f vector3f2 = vector3fs[k];
            vector3f2.rotate(quaternion2);
            vector3f2.scale(j);
            vector3f2.add(f, g, h);
        }

        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int light = 15728880;
        vertexConsumer.vertex((double)vector3fs[0].getX(), (double)vector3fs[0].getY(), (double)vector3fs[0].getZ()).texture(maxU, maxV).color(255, 255, 255, 255).light(light).next();
        vertexConsumer.vertex((double)vector3fs[1].getX(), (double)vector3fs[1].getY(), (double)vector3fs[1].getZ()).texture(maxU, minV).color(255, 255, 255, 255).light(light).next();
        vertexConsumer.vertex((double)vector3fs[2].getX(), (double)vector3fs[2].getY(), (double)vector3fs[2].getZ()).texture(minU, minV).color(255, 255, 255, 255).light(light).next();
        vertexConsumer.vertex((double)vector3fs[3].getX(), (double)vector3fs[3].getY(), (double)vector3fs[3].getZ()).texture(minU, maxV).color(255, 255, 255, 255).light(light).next();
    }

    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.markDead();
        }
    }
}
