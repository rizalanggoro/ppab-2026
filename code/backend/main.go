package main

import (
	"asprak/backend/config"
	_ "asprak/backend/docs"
	"asprak/backend/handler"
	"asprak/backend/model"
	"asprak/backend/repository"
	"log"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/joho/godotenv"
	fiberSwagger "github.com/swaggo/fiber-swagger"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

// @title 			asprak todo api
// @version 		1.0
// @host 				localhost:8080
// @basePath 		/
// @schemes 		http https
func main() {
	if err := godotenv.Load(); err != nil {
		log.Println("Warning: .env file not found, using environment variables")
	}

	config := config.New()

	db, err := gorm.Open(postgres.Open(config.GetDatabaseURL()))
	if err != nil {
		log.Fatalf("Failed to connect to database: %v", err)
	}

	if err := db.AutoMigrate(
		&model.User{},
		&model.Session{},
		&model.Todo{},
		&model.Category{},
	); err != nil {
		log.Fatalf("Failed to migrate database: %v", err)
	}

	app := fiber.New()
	app.Use(logger.New())
	app.Get("/swagger/*", fiberSwagger.WrapHandler)

	api := app.Group("/api")

	authRepo := repository.NewAuthRepository(db)
	authHandler := handler.NewAuthHandler(db, authRepo)
	authHandler.RegisterRoutes(api)

	categoryRepo := repository.NewCategoryRepository(db)
	categoryHandler := handler.NewCategoryHandler(db, categoryRepo)
	categoryHandler.RegisterRoutes(api)

	if err := app.Listen(":" + config.ServerPort); err != nil {
		log.Fatal(err)
	}
}
