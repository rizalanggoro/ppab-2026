package middleware

import (
	"asprak/backend/dto"
	"asprak/backend/model"
	"strings"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func AuthMiddleware(db *gorm.DB) fiber.Handler {
	return func(c *fiber.Ctx) error {
		authHeader := c.Get("Authorization")
		if authHeader == "" {
			return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
				Code:    fiber.StatusUnauthorized,
				Message: "Unauthorized",
			})
		}

		parts := strings.Split(authHeader, " ")
		if len(parts) != 2 || parts[0] != "Bearer" {
			return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
				Code:    fiber.StatusUnauthorized,
				Message: "invalid authorization header format",
			})
		}

		tokenString := parts[1]

		var session model.Session
		if err := db.Where("token = ?", tokenString).First(&session).Error; err != nil {
			return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
				Code:    fiber.StatusUnauthorized,
				Message: "invalid token",
			})
		}

		c.Locals("user_id", session.UserId)

		return c.Next()
	}
}

func GetUserID(c *fiber.Ctx) uint {
	userID, ok := c.Locals("user_id").(uint)
	if !ok {
		return 0
	}

	return userID
}
