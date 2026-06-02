package handler

import (
	"asprak/backend/domain"
	"asprak/backend/dto"
	"asprak/backend/middleware"
	"asprak/backend/repository"
	"asprak/backend/request"
	"asprak/backend/response"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

type CategoryHandler struct {
	db   *gorm.DB
	repo *repository.CategoryRepository
}

func NewCategoryHandler(
	db *gorm.DB,
	repo *repository.CategoryRepository,
) *CategoryHandler {
	return &CategoryHandler{
		db:   db,
		repo: repo,
	}
}

func (h *CategoryHandler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/categories").Use(middleware.AuthMiddleware(h.db))
	group.Post("/", h.create)
	group.Get("/", h.getAll)
	group.Delete("/:id", h.delete)
}

// @id 					create
// @tags 				category
// @accept 			json
// @produce 		json
// @param 			authorization header string true "authorization"
// @param 			body body request.CreateCategory true "body"
// @success 		200 {object} response.CreateCategory
// @failure 		500 {object} dto.Error
// @router 			/api/categories [post]
func (h *CategoryHandler) create(c *fiber.Ctx) error {
	userId := middleware.GetUserID(c)
	if userId == 0 {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
			Code:    fiber.StatusUnauthorized,
			Message: "Unauthorized",
		})
	}

	var req request.CreateCategory
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: err.Error(),
		})
	}

	res, err := h.repo.Create(domain.Category{
		UserId: userId,
		Name:   req.Name,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	return c.Status(fiber.StatusCreated).JSON(response.CreateCategory{
		Item: dto.Category{
			Data: *res,
		},
	})
}

// @id 					getAll
// @tags 				category
// @accept 			json
// @produce 		json
// @param 			authorization header string true "authorization"
// @success 		200 {object} response.GetAllCategories
// @failure 		500 {object} dto.Error
// @router 			/api/categories [get]
func (h *CategoryHandler) getAll(c *fiber.Ctx) error {
	userId := middleware.GetUserID(c)
	if userId == 0 {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
			Code:    fiber.StatusUnauthorized,
			Message: "Unauthorized",
		})
	}

	res, err := h.repo.GetAll(repository.GetAllCategoriesFilter{
		UserId: userId,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	categories := make([]dto.Category, len(res))
	for i, category := range res {
		categories[i] = dto.Category{
			Data: category,
		}
	}

	return c.Status(fiber.StatusOK).JSON(response.GetAllCategories{
		Items: categories,
	})
}

// @id 					delete
// @tags 				category
// @accept 			json
// @produce 		json
// @param 			authorization header string true "authorization"
// @param 			id path int true "id"
// @success 		200 {object} response.DeleteCategory
// @failure 		500 {object} dto.Error
// @router 			/api/categories/{id} [delete]
func (h *CategoryHandler) delete(c *fiber.Ctx) error {
	categoryId, err := c.ParamsInt("id")
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: err.Error(),
		})
	}

	if err := h.repo.Delete(uint(categoryId)); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	return c.Status(fiber.StatusOK).JSON(response.DeleteCategory{
		Message: "Category deleted successfully",
	})
}
