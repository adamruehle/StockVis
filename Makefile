.PHONY: run install clean

ifeq ($(OS),Windows_NT)
    # Windows commands
    FRONTEND_RUN := cd frontend && start /b npm run dev
    BACKEND_RUN := cd backend/stockvis && start /b mvnw.cmd spring-boot:run
    FRONTEND_INSTALL := cd frontend && npm install
    BACKEND_INSTALL := cd backend/stockvis && mvnw.cmd clean install
    FRONTEND_CLEAN := cd frontend && rmdir /s /q .next node_modules
    BACKEND_CLEAN := cd backend/stockvis && mvnw.cmd clean
else
    FRONTEND_RUN := cd frontend && npm run dev &
    BACKEND_RUN := cd backend/stockvis && ./mvnw spring-boot:run &
    FRONTEND_INSTALL := cd frontend && npm install
    BACKEND_INSTALL := cd backend/stockvis && ./mvnw clean install
    FRONTEND_CLEAN := cd frontend && rm -rf .next node_modules
    BACKEND_CLEAN := cd backend/stockvis && ./mvnw clean
endif

run:
	@echo "Starting StockVis application..."
	$(FRONTEND_RUN)
	$(BACKEND_RUN)

install:
	@echo "Installing dependencies..."
	$(FRONTEND_INSTALL)
	$(BACKEND_INSTALL)

clean:
	@echo "Cleaning build files..."
	$(FRONTEND_CLEAN)
	$(BACKEND_CLEAN)