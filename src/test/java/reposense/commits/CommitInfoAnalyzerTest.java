package reposense.commits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.commits.model.ContributionPair;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.FileType;
import reposense.model.FileTypeTest;
import reposense.template.GitTestTemplate;
import reposense.util.SystemUtil;

public class CommitInfoAnalyzerTest extends GitTestTemplate {
    private static final int NUMBER_EUGENE_COMMIT = 1;
    private static final int NUMBER_MINGYI_COMMIT = 1;
    private static final int NUMBER_EMPTY_MESSAGE_COMMIT = 1;
    private static final FileType FILETYPE_JAVA = new FileType("java", Collections.singletonList("**java"));
    private static final FileType FILETYPE_MD = new FileType("md", Collections.singletonList("**md"));
    private static final FileType FILETYPE_JSON = new FileType("json", Collections.singletonList("**json"));
    private static final FileType FILETYPE_TXT = new FileType("txt", Collections.singletonList("**txt"));
    private static final String DUPLICATE_AUTHORS_DUPLICATE_COMMITS_HASH = "f34c20ec2c3be63e0764d4079a575dd75269ffeb";

    @Before
    public void before() throws Exception {
        super.before();
        config.getAuthorDetailsToAuthorMap().clear();
    }

    @Test
    public void analyzeCommits_allAuthorNoIgnoredCommitsNoDateRange_success() {
        config.getAuthorDetailsToAuthorMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorDetailsToAuthorMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        config.getAuthorDetailsToAuthorMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitInfos.size(), commitResults.size());
    }

    @Test
    public void analyzeCommits_fakeMainAuthorNoIgnoredCommitsNoDateRange_success() {
        config.getAuthorDetailsToAuthorMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorDetailsToAuthorMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitInfos.size() - NUMBER_EUGENE_COMMIT, commitResults.size());
    }

    @Test
    public void analyzeCommits_eugeneAuthorNoIgnoredCommitsNoDateRange_success() {
        config.getAuthorDetailsToAuthorMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(NUMBER_EUGENE_COMMIT, commitResults.size());
    }

    @Test
    public void analyzeCommits_allAuthorSingleCommitIgnoredNoDateRange_success() {
        config.getAuthorDetailsToAuthorMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorDetailsToAuthorMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        config.getAuthorDetailsToAuthorMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        config.setIgnoreCommitList(Collections.singletonList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018));
        List<CommitResult> commitResultsFull = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);
        config.setIgnoreCommitList(
                Collections.singletonList(
                        new CommitHash(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8))));
        List<CommitResult> commitResultsShort = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitResultsShort, commitResultsFull);
        Assert.assertEquals(commitInfos.size() - 1, commitResultsFull.size());
    }

    @Test
    public void analyzeCommits_allAuthorMultipleCommitIgnoredNoDateRange_success() {
        config.getAuthorDetailsToAuthorMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorDetailsToAuthorMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        config.getAuthorDetailsToAuthorMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        config.setIgnoreCommitList(
                Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018, EUGENE_AUTHOR_README_FILE_COMMIT_07052018));
        List<CommitResult> commitResultsFull = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);
        config.setIgnoreCommitList(CommitHash.convertStringsToCommits(Arrays.asList(
                FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8),
                EUGENE_AUTHOR_README_FILE_COMMIT_07052018_STRING.substring(0, 8))));
        List<CommitResult> commitResultsShort = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitResultsShort, commitResultsFull);
        Assert.assertEquals(commitInfos.size() - 2, commitResultsFull.size());
    }

    @Test
    public void analyzeCommits_noCommitMessage_success() {
        config.setBranch("empty-commit-message");
        config.getAuthorDetailsToAuthorMap().clear();
        config.getAuthorDetailsToAuthorMap().put(YONG_AUTHOR_NAME, new Author(YONG_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);
        commitResults.removeIf(s -> !s.getMessageTitle().isEmpty());

        Assert.assertEquals(NUMBER_EMPTY_MESSAGE_COMMIT, commitResults.size());
    }

    @Test
    public void analyzeCommits_emailWithAdditionOperator_success() {
        config.setBranch("617-FileAnalyzerTest-analyzeFile_emailWithAdditionOperator_success");
        Author author = new Author(MINGYI_AUTHOR_NAME);
        config.setAuthorList(Collections.singletonList(author));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(NUMBER_MINGYI_COMMIT, commitResults.size());
    }

    @Test
    public void analyzeCommits_duplicateAuthorsDuplicateCommits_success() throws Exception {
        Author author = new Author(FAKE_AUTHOR_NAME);
        List<CommitResult> expectedCommitResults = new ArrayList<>();
        Map<FileType, ContributionPair> fileTypeAndContributionMap = new HashMap<>();
        fileTypeAndContributionMap.put(FILETYPE_JAVA, new ContributionPair(3, 3));
        expectedCommitResults.add(new CommitResult(author, DUPLICATE_AUTHORS_DUPLICATE_COMMITS_HASH,
                parseGitStrictIsoDate("2021-08-03T12:53:39+08:00"),
                "Update annotationTest.java",
                "", null, fileTypeAndContributionMap));

        config.setAuthorList(Arrays.asList(author, author));
        config.setSinceDate(new GregorianCalendar(2021, Calendar.AUGUST, 3).getTime());
        config.setUntilDate(new GregorianCalendar(2021, Calendar.AUGUST, 4).getTime());

        List<CommitInfo> actualCommitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> actualCommitResults = CommitInfoAnalyzer.analyzeCommits(actualCommitInfos, config);

        Assert.assertEquals(actualCommitInfos.size(), 2);
        Assert.assertEquals(expectedCommitResults, actualCommitResults);
    }

    @Test
    public void analyzeCommits_multipleCommitsWithCommitMessageBody_success() throws Exception {
        Author author = new Author(JINYAO_AUTHOR_NAME);
        List<CommitResult> expectedCommitResults = new ArrayList<>();

        Map<FileType, ContributionPair> firstFileTypeAndContributionMap = new HashMap<>();
        firstFileTypeAndContributionMap.put(FILETYPE_JAVA, new ContributionPair(1, 0));

        Map<FileType, ContributionPair> secondFileTypeAndContributionMap = new HashMap<>();
        secondFileTypeAndContributionMap.put(FILETYPE_JAVA, new ContributionPair(0, 1));

        expectedCommitResults.add(new CommitResult(author, "2eccc111e813e8b2977719b5959e32b674c56afe",
                parseGitStrictIsoDate("2019-06-19T13:02:01+08:00"), ">>>COMMIT INFO<<<",
                "Hi there!\n\n>>>COMMIT INFO<<<\n", null,
                firstFileTypeAndContributionMap));
        expectedCommitResults.add(new CommitResult(author, "8f8359649361f6736c31b87d499a4264f6cf7ed7",
                parseGitStrictIsoDate("2019-06-19T13:03:39+08:00"), "[#123] Reverted 1st commit",
                "This is a test to see if the commit message body works. "
                + "All should be same same.\n>>>COMMIT INFO<<<\n|The end.", null,
                secondFileTypeAndContributionMap));

        config.setBranch("751-CommitInfoAnalyzerTest-analyzeCommits_multipleCommitsWithCommitMessageBody_success");
        config.setAuthorList(Collections.singletonList(author));
        config.setSinceDate(new GregorianCalendar(2019, Calendar.JUNE, 19).getTime());
        config.setUntilDate(new GregorianCalendar(2019, Calendar.JUNE, 20).getTime());

        List<CommitInfo> actualCommitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> actualCommitResults = CommitInfoAnalyzer.analyzeCommits(actualCommitInfos, config);

        Assert.assertEquals(expectedCommitResults, actualCommitResults);
    }

    @Test
    public void analyzeCommits_commitsWithEmptyCommitMessageTitleOrBody_success() throws Exception {
        Author author = new Author(JINYAO_AUTHOR_NAME);
        List<CommitResult> expectedCommitResults = new ArrayList<>();

        Map<FileType, ContributionPair> firstFileTypeAndContributionMap = new HashMap<>();
        firstFileTypeAndContributionMap.put(FILETYPE_JAVA, new ContributionPair(1, 0));

        Map<FileType, ContributionPair> secondFileTypeAndContributionMap = new HashMap<>();
        secondFileTypeAndContributionMap.put(FILETYPE_JAVA, new ContributionPair(0, 1));

        // 1st test: Contains commit message title but no commit message body.
        expectedCommitResults.add(new CommitResult(author, "e54ae8fdb77c6c7d2c39131b816bfc03e6a6dd44",
                parseGitStrictIsoDate("2019-07-02T12:35:46+08:00"), "Test 1: With message title but no body",
                "", null, firstFileTypeAndContributionMap));
        // 2nd test: Contains no commit message title and no commit message body.
        expectedCommitResults.add(new CommitResult(author, "57fa22fc2550210203c2941692f69ccb0cf18252",
                parseGitStrictIsoDate("2019-07-02T12:36:14+08:00"), "", "", null,
                secondFileTypeAndContributionMap));

        config.setBranch("751-CommitInfoAnalyzerTest-analyzeCommits_commitsWithEmptyCommitMessageTitleOrBody_success");
        config.setAuthorList(Collections.singletonList(author));
        config.setSinceDate(new GregorianCalendar(2019, Calendar.JULY, 2).getTime());
        config.setUntilDate(new GregorianCalendar(2019, Calendar.JULY, 3).getTime());

        List<CommitInfo> actualCommitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> actualCommitResults = CommitInfoAnalyzer.analyzeCommits(actualCommitInfos, config);

        Assert.assertEquals(expectedCommitResults, actualCommitResults);
    }

    @Test
    public void analyzeCommits_commitsWithMultipleTags_success() throws Exception {
        Author author = new Author(JAMES_AUTHOR_NAME);
        List<CommitResult> expectedCommitResults = new ArrayList<>();

        Map<FileType, ContributionPair> firstFileTypeAndContributionMap = new HashMap<>();
        firstFileTypeAndContributionMap.put(FILETYPE_MD, new ContributionPair(2, 1));

        Map<FileType, ContributionPair> secondFileTypeAndContributionMap = new HashMap<>();
        secondFileTypeAndContributionMap.put(FILETYPE_MD, new ContributionPair(1, 0));

        expectedCommitResults.add(new CommitResult(author, "62c3a50ef9b3580b2070deac1eed2b3e2d701e04",
                parseGitStrictIsoDate("2019-12-20T22:45:18+08:00"), "Single Tag Commit",
                "", new String[] {"1st"}, firstFileTypeAndContributionMap));
        expectedCommitResults.add(new CommitResult(author, "c5e36ec059390233ac036db61a84fa6b55952506",
                parseGitStrictIsoDate("2019-12-20T22:47:21+08:00"), "Double Tag Commit",
                "", new String[] {"2nd-tag", "1st-tag"}, secondFileTypeAndContributionMap));

        config.setBranch("879-CommitInfoAnalyzerTest-analyzeCommits_commitsWithMultipleTags_success");
        config.setAuthorList(Collections.singletonList(author));
        config.setSinceDate(new GregorianCalendar(2019, Calendar.DECEMBER, 20).getTime());
        config.setUntilDate(new GregorianCalendar(2019, Calendar.DECEMBER, 21).getTime());

        List<CommitInfo> actualCommitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> actualCommitResults = CommitInfoAnalyzer.analyzeCommits(actualCommitInfos, config);

        Assert.assertEquals(expectedCommitResults, actualCommitResults);
    }

    @Test
    public void analyzeCommits_emptyCommits_success() throws Exception {
        Author author = new Author(JAMES_AUTHOR_NAME);
        List<CommitResult> expectedCommitResults = new ArrayList<>();

        expectedCommitResults.add(new CommitResult(author, "016ab87c4afe89a98225b96c98ff28dd4774410f",
                parseGitStrictIsoDate("2020-01-27T22:20:51+08:00"), "empty commit", "", null));

        config.setBranch("1019-CommitInfoAnalyzerTest-emptyCommits");
        config.setAuthorList(Collections.singletonList(author));
        config.setFormats(FileTypeTest.NO_SPECIFIED_FORMATS);
        config.setSinceDate(new GregorianCalendar(2020, Calendar.JANUARY, 27).getTime());
        config.setUntilDate(new GregorianCalendar(2020, Calendar.JANUARY, 28).getTime());

        List<CommitInfo> actualCommitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> actualCommitResults = CommitInfoAnalyzer.analyzeCommits(actualCommitInfos, config);

        Assert.assertEquals(expectedCommitResults, actualCommitResults);
    }

    @Test
    public void analyzeCommits_commitsWithBinaryFileContribution_success() throws Exception {
        Author author = new Author(JAMES_AUTHOR_NAME);
        List<CommitResult> expectedCommitResults = new ArrayList<>();

        // binary file contribution will have 0 contribution and won't be added to fileTypesAndContributionMap
        expectedCommitResults.add(new CommitResult(author, "a00c51138cbf5ab7d14f52b52abb182c8a369169",
                parseGitStrictIsoDate("2020-04-06T16:41:10+08:00"), "Add binary file", "", null));

        config.setBranch("1192-CommitInfoAnalyzerTest-analyzeCommits_commitsWithBinaryContribution_success");
        config.setAuthorList(Collections.singletonList(author));
        config.setFormats(FileTypeTest.NO_SPECIFIED_FORMATS);
        config.setSinceDate(new GregorianCalendar(2020, Calendar.APRIL, 6).getTime());
        config.setUntilDate(new GregorianCalendar(2020, Calendar.APRIL, 7).getTime());

        List<CommitInfo> actualCommitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> actualCommitResults = CommitInfoAnalyzer.analyzeCommits(actualCommitInfos, config);

        Assert.assertEquals(expectedCommitResults, actualCommitResults);
    }

    @Test
    public void analyzeCommits_fileNameWithSpecialChars_success() throws Exception {
        // Runs test only on non Windows (Unix) operating systems as the file names are invalid in windows
        Assume.assumeTrue(!SystemUtil.isWindows());

        Author author = new Author(JAMES_ALTERNATIVE_AUTHOR_NAME);
        List<CommitResult> expectedCommitResults = new ArrayList<>();
        Map<FileType, ContributionPair> firstFileTypeAndContributionMap = new HashMap<>();
        firstFileTypeAndContributionMap.put(FILETYPE_TXT, new ContributionPair(1, 0));
        expectedCommitResults.add(new CommitResult(author, "cfb3c8dc477cb0af19fce8bead4d278f35afa396",
                parseGitStrictIsoDate("2020-04-20T12:09:39+08:00"),
                "Create file name without special chars",
                "", null, firstFileTypeAndContributionMap));
        Map<FileType, ContributionPair> secondFileTypeAndContributionMap = new HashMap<>();
        secondFileTypeAndContributionMap.put(FILETYPE_TXT, new ContributionPair(0, 0));
        expectedCommitResults.add(new CommitResult(author, "17bde492e9a80d8699ad193cf87e677341f936cc",
                parseGitStrictIsoDate("2020-04-20T12:17:40+08:00"),
                "Rename to file name with special chars",
                "", null, secondFileTypeAndContributionMap));

        config.setBranch("1244-CommitInfoAnalyzerTest-analyzeCommits_fileNameWithSpecialChars_success");
        config.setAuthorList(Collections.singletonList(author));
        config.setSinceDate(new GregorianCalendar(2020, Calendar.APRIL, 20).getTime());
        config.setUntilDate(new GregorianCalendar(2020, Calendar.APRIL, 21).getTime());

        List<CommitInfo> actualCommitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> actualCommitResults = CommitInfoAnalyzer.analyzeCommits(actualCommitInfos, config);

        Assert.assertEquals(2, actualCommitInfos.size());
        Assert.assertEquals(expectedCommitResults, actualCommitResults);
    }

    /**
     * Returns a {@code Date} from a string {@code gitStrictIsoDate}.
     */
    private Date parseGitStrictIsoDate(String gitStrictIsoDate) throws Exception {
        return CommitInfoAnalyzer.GIT_STRICT_ISO_DATE_FORMAT.parse(gitStrictIsoDate);
    }
}
