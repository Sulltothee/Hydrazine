package Utility;

public class Vec2 {
    public float x = 0.0F;
    public float y = 0.0F;
    public final static Vec2 Zero()  {return new  Vec2(0.0F, 0.0F);}
    public final static Vec2 Up(){return new Vec2(0.0F, -1.0F);}
    public final static Vec2 Down() {return new Vec2(0.0F, 1.0F);}
    public final static Vec2 Left() {return new Vec2(-1.0F, 0.0F);}
    public final static Vec2 Right() {return new Vec2(1.0F, 0.0F);}

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2(float xy) {
        this.x = xy;
        this.y = xy;
    }

    public boolean Clamp(float xMin, float xMax, float yMin, float yMax) {
        boolean clamped = false;
        if (this.x < xMin) {
            this.x = xMin;
            clamped = true;
        }

        if (this.x > xMax) {
            this.x = xMax;
            clamped = true;
        }

        if (this.y < yMin) {
            this.y = yMin;
            clamped = true;
        }

        if (this.y > yMax) {
            this.y = yMax;
            clamped = true;
        }

        return clamped;
    }

    public Vec2 getClamped(float xMin, float xMax, float yMin, float yMax) {
        Vec2 tempVector = new Vec2(this.x, this.y);
        tempVector.Clamp(xMin, xMax, yMin, yMax);
        return tempVector;
    }

    public Vec2 getNormalized() {
        return (double)this.getMagnitude() > 1.0E-5 ? new Vec2(this.x / this.getMagnitude(), this.y / this.getMagnitude()) : this;
    }

    public float getMagnitude() {
        return (float)Math.sqrt(Math.pow(this.x, 2.0F) + Math.pow(this.y, 2.0F));
    }

    public Vec2 invert() {
        return new Vec2(this.x * -1.0F, this.y * -1.0F);
    }

    public static Vec2 Add(Vec2 v1, Vec2 v2) {
        float X = v1.x + v2.x;
        float Y = v1.y + v2.y;
        return new Vec2(X, Y);
    }

    public static Vec2 Add(Vec2[] vectors) {
        try {
            Vec2 total = Add(vectors[0], vectors[1]);

            for(int i = 2; i < vectors.length; ++i) {
                total = Add(total, vectors[i]);
            }

            return total;
        } catch (Exception var3) {
            return null;
        }
    }

    public static Vec2 Subtract(Vec2 vPositive, Vec2 vNegative) {
        Vec2 vFlipped = vNegative.invert();
        return Add(vPositive, vFlipped);
    }

    public static Vec2 Scale(Vec2 vector, float scalar) {
        return new Vec2(vector.x * scalar, vector.y * scalar);
    }

    public static float Dot(Vec2 v1, Vec2 v2) {
        return (v1.x * v2.x) + (v1.y * v2.y);
    }

    public static Vec2 DotVector(Vec2 v1, Vec2 v2) {
        return new Vec2 ((v1.x * v2.x) , (v1.y * v2.y));
    }

    public void output(String header) {
        System.out.println(header + ": (" + this.x + "," + this.y + ")");
    }

    public boolean equals(Vec2 other) {
        return MiscFunctions.RoundToNDecimalPlaces(this.x, 5) == MiscFunctions.RoundToNDecimalPlaces(other.x, 5) && MiscFunctions.RoundToNDecimalPlaces(this.y, 5) == MiscFunctions.RoundToNDecimalPlaces(other.y, 5);
    }

    public static Vec2 SetMagnitude(Vec2 vector, float magnitude){
        float CurrentMagnitude = vector.getMagnitude();

        vector = Vec2.Scale(vector, magnitude/CurrentMagnitude);

        return vector;
    }

    public static Vec2 Copy(Vec2 vector){
        return new Vec2(vector.x, vector.y);
    }

    //Gets the closest point on the line (a->b) to position
    public static Vec2 getClosestOnEdge(Vec2 a, Vec2 b, Vec2 position) {
        Vec2 ab = Vec2.Subtract(b, a);
        Vec2 V = new Vec2(ab.y, ab.x * -1.0F);

        float distance =  Vec2.GetDistanceFromLine(a, b, position);
        Vec2 d = Vec2.Subtract(position, Vec2.Scale(V.getNormalized(), distance));
        Vec2 ad = Vec2.Subtract(d, Vec2.Add(a, b));

        if (ad.getNormalized().equals(ab.getNormalized()) && ad.getMagnitude() > ab.getMagnitude()) {
            d = Vec2.Add(b, a);
        } else if (ad.getNormalized().equals(ab.getNormalized().invert())) {
            d = Vec2.Add(a, b);
        }

        return d;
    }

    public static float GetDistanceFromLine(Vec2 a, Vec2 b, Vec2 position) {
        Vec2 ab = Vec2.Subtract(b, a);
        Vec2 V = new Vec2(ab.y, ab.x * -1.0F);
        Vec2 U = Vec2.Subtract(position, a);
        return Vec2.Dot(U, V) / V.getMagnitude();
    }

    //Gets the clockwise normal from a vector (a->b)
    public static Vec2 normalFromLine(Vec2 a, Vec2 b) {
        Vec2 ab = Vec2.Subtract(b, a);
        Vec2 V = new Vec2(ab.y, ab.x * -1.0F);
        return V;
    }

    public static boolean isInEdge(Vec2 a, Vec2 b, Vec2 position) {
        return 0.0F <= Vec2.GetDistanceFromLine(a, b, position);
    }
}