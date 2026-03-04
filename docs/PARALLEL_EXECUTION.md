# Parallel Execution Strategy

## Overview
The framework supports multiple parallel execution strategies optimized for different test types.

## Execution Modes

### 1. **testng.xml** - Class-Level Parallelism
- **Parallel Level**: `classes`
- **Thread Count**: 5
- **Best For**: Full test suite with mixed test types
- **Behavior**: Each test class runs in parallel, methods within a class run sequentially

### 2. **testng_parallel.xml** - Optimized Parallel Suite ⭐ RECOMMENDED
- **Parallel Level**: Mixed (classes + methods + tests)
- **Thread Count**: 8 (suite), 5 (CRUD), 4 (assignments)
- **Best For**: Maximum speed with proper test isolation
- **Strategy**:
  - CRUD tests: Parallel at method level (independent operations)
  - E2E flows: Sequential within each flow, but flows run in parallel
  - Assignment tests: Parallel at class level

### 3. **testng_reg.xml** - Regression Suite
- **Parallel Level**: `classes`
- **Thread Count**: 4
- **Best For**: Regression testing with grouped tests

### 4. **testng_E2E.xml** - E2E Suite
- **Parallel Level**: `tests`
- **Thread Count**: 2
- **Best For**: End-to-end flows with dependencies
- **Behavior**: Each test group runs in parallel, but methods within maintain order

## Thread Configuration

| Suite | Suite Threads | Test Threads | Data Provider Threads |
|-------|--------------|--------------|----------------------|
| testng.xml | 5 | - | 3 |
| testng_parallel.xml | 8 | 4-5 | 4 |
| testng_reg.xml | 4 | - | 2 |
| testng_E2E.xml | 2 | - | - |

## Execution Commands

```bash
# Windows
mvnw.cmd test -DsuiteXmlFile=testng_parallel.xml -Denv=dev

# Linux/macOS
./mvnw test -DsuiteXmlFile=testng_parallel.xml -Denv=dev
```

## Performance Comparison

| Suite | Estimated Time | Parallelism |
|-------|---------------|-------------|
| Sequential | ~5-8 min | None |
| testng.xml | ~2-3 min | Medium |
| testng_parallel.xml | ~1-2 min | High |

## Best Practices

1. **Independent Tests**: Use method-level parallelism
2. **Dependent Tests**: Use test-level parallelism with preserve-order
3. **Data Providers**: Configure separate thread pool
4. **Thread Safety**: Ensure ThreadLocal usage in BaseTest

## Thread Safety Checklist

✅ RequestSpecification - ThreadLocal in BaseTest
✅ Response objects - Method scoped
✅ Test data - Generated per test
✅ Tokens - Generated per test or cached with thread safety

## Troubleshooting

**Issue**: Tests fail in parallel but pass sequentially
- **Cause**: Shared state or race conditions
- **Fix**: Check for static variables, ensure ThreadLocal usage

**Issue**: Slower than expected
- **Cause**: Too many threads causing context switching
- **Fix**: Reduce thread count to CPU cores + 1

**Issue**: E2E tests interfere with each other
- **Cause**: Same booking IDs or tokens
- **Fix**: Use unique test data per test instance
