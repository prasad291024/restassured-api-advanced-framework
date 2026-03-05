# CI/CD Workflows

## Overview
The framework includes three GitHub Actions workflows for automated testing and reporting.

## Workflows

### 1. API Framework CI (`ci.yml`)
**Triggers:**
- Push to `master`, `main`, or `develop` branches
- Pull requests to `master` or `main`
- Manual trigger via workflow_dispatch

**Features:**
- ✅ Matrix strategy: Runs both `testng.xml` and `testng_parallel.xml`
- ✅ Maven dependency caching
- ✅ Allure report generation
- ✅ Test result artifacts (30-day retention)
- ✅ Test summary in PR comments
- ✅ Separate retry verification job

**Artifacts Generated:**
- `allure-report-{suite}` - HTML Allure reports
- `test-results-{suite}` - TestNG/Surefire reports

### 2. Publish Allure Report (`publish-report.yml`)
**Triggers:**
- After CI workflow completes (success or failure)

**Features:**
- ✅ Publishes Allure reports to GitHub Pages
- ✅ Automatic deployment to `gh-pages` branch
- ✅ Historical report tracking

**Access Report:**
`https://{username}.github.io/{repo}/docs/allure-report/`

### 3. Scheduled Regression Tests (`regression.yml`)
**Triggers:**
- Daily at 2 AM UTC (cron schedule)
- Manual trigger via workflow_dispatch

**Features:**
- ✅ Runs regression suite (`testng_reg.xml`)
- ✅ 90-day artifact retention
- ✅ Failure notifications
- ✅ Maven caching for faster execution

## Workflow Diagram

```
┌─────────────────┐
│  Code Push/PR   │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│   API Framework CI          │
│  ┌─────────────────────┐   │
│  │ Build & Test Matrix │   │
│  │  - testng.xml       │   │
│  │  - testng_parallel  │   │
│  └─────────────────────┘   │
│  ┌─────────────────────┐   │
│  │ Retry Verification  │   │
│  └─────────────────────┘   │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  Publish Allure Report      │
│  → GitHub Pages             │
└─────────────────────────────┘

┌─────────────────┐
│  Daily 2 AM UTC │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│  Scheduled Regression       │
│  → testng_reg.xml           │
└─────────────────────────────┘
```

## Cache Strategy

### Maven Dependencies
- **Path**: `~/.m2/repository`, `target/`
- **Key**: `${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}`
- **Benefit**: 2-3x faster builds

### Java Setup
- **Built-in cache**: Enabled via `cache: maven`
- **Distribution**: Temurin JDK 22

## Test Result Summary

The CI automatically generates test summaries in PR comments:

| Metric | Count |
|--------|-------|
| Total | 45 |
| ✅ Passed | 42 |
| ❌ Failed | 2 |
| ⏭️ Skipped | 1 |

## Artifacts & Retention

| Artifact Type | Retention | Workflow |
|--------------|-----------|----------|
| Allure Reports | 30 days | CI |
| Test Results | 30 days | CI |
| Regression Reports | 90 days | Regression |

## Manual Workflow Trigger

### Via GitHub UI:
1. Go to **Actions** tab
2. Select workflow (CI / Regression)
3. Click **Run workflow**
4. Choose branch and click **Run**

### Via GitHub CLI:
```bash
# Trigger CI workflow
gh workflow run ci.yml

# Trigger regression tests
gh workflow run regression.yml
```

## Environment Variables

Set these in GitHub repository secrets if needed:

```yaml
# For QA/Prod environments
AUTH_CLIENT_ID
AUTH_CLIENT_SECRET
AUTH_USERNAME
AUTH_PASSWORD
```

## Troubleshooting

### Issue: Cache not working
**Solution**: Clear cache via GitHub Actions settings or update cache key

### Issue: Allure report not generated
**Solution**: Check `allure-results/` directory exists and contains JSON files

### Issue: Test summary not showing
**Solution**: Ensure `testng-results.xml` is generated in `target/surefire-reports/`

### Issue: Workflow fails on fork
**Solution**: Forks need to enable GitHub Actions in repository settings

## Best Practices

1. **Always review test results** before merging PRs
2. **Check Allure reports** for detailed failure analysis
3. **Monitor regression tests** daily for early issue detection
4. **Use workflow_dispatch** for on-demand test execution
5. **Keep artifacts** for critical releases (download before expiry)

## Performance Metrics

| Workflow | Avg Duration | Cache Hit | Cache Miss |
|----------|-------------|-----------|------------|
| CI (testng.xml) | 3-4 min | 2-3 min | 5-6 min |
| CI (parallel) | 2-3 min | 1-2 min | 4-5 min |
| Regression | 4-5 min | 3-4 min | 6-7 min |

## Future Enhancements

- [ ] Slack/Email notifications
- [ ] Code coverage reports (JaCoCo)
- [ ] Performance trend analysis
- [ ] Multi-environment testing (dev/qa/staging)
- [ ] Docker container execution
