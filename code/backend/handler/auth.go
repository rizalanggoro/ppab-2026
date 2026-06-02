package handler

import (
	"asprak/backend/domain"
	"asprak/backend/dto"
	"asprak/backend/repository"
	"asprak/backend/request"
	"asprak/backend/response"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

type AuthHandler struct {
	db   *gorm.DB
	repo *repository.AuthRepository
}

func NewAuthHandler(
	db *gorm.DB,
	repo *repository.AuthRepository,
) *AuthHandler {
	return &AuthHandler{
		db:   db,
		repo: repo,
	}
}

func (h *AuthHandler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/auth")
	group.Post("/login", h.login)
	group.Post("/register", h.register)
}

// @id 					login
// @tags 				auth
// @accept 			json
// @produce 		json
// @param 			body body request.Login true "body"
// @success 		200 {object} response.Login
// @failure 		500 {object} dto.Error
// @router 			/api/auth/login [post]
func (h *AuthHandler) login(c *fiber.Ctx) error {
	var req request.Login
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: err.Error(),
		})
	}

	res, err := h.repo.Login(domain.User{
		Email:    req.Email,
		Password: req.Password,
	})
	if err != nil {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
			Code:    fiber.StatusUnauthorized,
			Message: "Invalid email or password",
		})
	}

	return c.Status(fiber.StatusOK).JSON(response.Login{
		Token: *res,
	})
}

// @id 					register
// @tags 				auth
// @accept 			json
// @produce 		json
// @param 			body body request.Register true "body"
// @success 		200 {object} response.Register
// @failure 		500 {object} dto.Error
// @router 			/api/auth/register [post]
func (h *AuthHandler) register(c *fiber.Ctx) error {
	var req request.Register
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: err.Error(),
		})
	}

	res, err := h.repo.Register(domain.User{
		Name:     req.Name,
		Email:    req.Email,
		Password: req.Password,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	return c.Status(fiber.StatusOK).JSON(response.Register{
		Token: *res,
	})
}
