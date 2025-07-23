db = db.getSiblingDB('community_db');

db.resource_points.insertMany([
    { "name": "doctors", "point": 4 },
    { "name": "volunteers", "point": 3 },
    { "name": "medicalSuppliesKits", "point": 7 },
    { "name": "transportVehicles", "point": 5 },
    { "name": "basicFoodBaskets", "point": 2 }
]);