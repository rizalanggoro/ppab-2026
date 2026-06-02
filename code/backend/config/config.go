package config

import (
	"fmt"
	"os"
)

type Config struct {
	ServerPort  string
	ServerEnv   string
	DBHost      string
	DBPort      string
	DBUser      string
	DBPassword  string
	DBName      string
	AppTimeZone string
}

func New() *Config {
	return &Config{
		ServerPort:  getEnv("SERVER_PORT", "8080"),
		ServerEnv:   getEnv("SERVER_ENV", "development"),
		DBHost:      getEnv("DB_HOST", "localhost"),
		DBPort:      getEnv("DB_PORT", "5432"),
		DBUser:      getEnv("DB_USER", "postgres"),
		DBPassword:  getEnv("DB_PASSWORD", "postgres"),
		DBName:      getEnv("DB_NAME", "asprak_todo"),
		AppTimeZone: getEnv("APP_TIMEZONE", "Asia/Jakarta"),
	}
}

func (c *Config) GetDatabaseURL() string {
	return fmt.Sprintf(
		"host=%s port=%s user=%s password=%s dbname=%s sslmode=disable TimeZone=%s",
		c.DBHost,
		c.DBPort,
		c.DBUser,
		c.DBPassword,
		c.DBName,
		c.AppTimeZone,
	)
}

func getEnv(key, defaultValue string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return defaultValue
}
