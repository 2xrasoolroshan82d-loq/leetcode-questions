    private int[] count;
    public ParkingSystem(int big, int medium, int small) {
        count = new int[]{0, big, medium, small};
    }
    
    public boolean addCar(int carType) {
        return count[carType]-- > 0;
    }
