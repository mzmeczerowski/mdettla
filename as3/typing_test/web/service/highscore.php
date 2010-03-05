<?php

include '../include/log.php';
include '../include/utils.php';

session_start();

function validate($username, $speed, $mistakes, $corrections,
        $pl, $chars, $minutes, $seconds) {
    return !empty($username) && strlen($username) <= 32
        && strlen($username) >= 3 && is_numeric(str_replace(',', '.', $speed))
        && is_numeric($mistakes) && is_numeric($corrections)
        && ($pl == 'true' || $pl == 'false') && is_numeric($chars)
        && is_numeric($minutes) && is_numeric($seconds);
}

function isSubmittedTooSoon($submit_time, $last_submit_time) {
    $min_time_between_submits = 30;
    return (is_numeric($last_submit_time)
        && ($submit_time - $last_submit_time < $min_time_between_submits));
}

$HIGHSCORE_SIZE = 25;
$MIN_REQUIRED_SPEED = 200;

$result = pg_query("
    SELECT
        MIN(speed) AS required_speed,
        COUNT(speed) AS current_size
        FROM (
            SELECT speed
                FROM tt.highscore
                ORDER BY speed DESC
                LIMIT $HIGHSCORE_SIZE
        ) AS highscore
") or log_write("ERROR: problem with query: $query ("
        . pg_last_error() . ')');
$row = pg_fetch_assoc($result);
if ($row && $row['current_size'] == $HIGHSCORE_SIZE) {
    $required_speed = $row['required_speed'];
}
if (empty($required_speed)) {
    $required_speed = $MIN_REQUIRED_SPEED;
}
if ($_GET['q'] == 'get_threshold') {
    $_SESSION['hs_h_data'] = rand_str(32);
    header('Content-Type: text/xml');
    echo "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
    echo "<response>\n";
    echo '<requiredSpeed>' . $required_speed . "</requiredSpeed>\n";
    echo '<hData>' . $_SESSION['hs_h_data'] . "</hData>\n";
    echo '</response>';
} else {
    $username = $_POST['username'];
    $speed = $_POST['speed'];
    $mistakes = $_POST['mistakes'];
    $corrections = $_POST['corrections'];
    $pl = $_POST['plChars'];
    $chars = $_POST['correctChars'];
    $minutes = $_POST['minutes'];
    $seconds = $_POST['seconds'];
    $h = $_POST['h'];

    $MAX_MISTAKES = 0;

    $current_time = time();
    if (!validate($username, $speed, $mistakes, $corrections,
            $pl, $chars, $minutes, $seconds)) {
        echo 'Does not compute.';
        log_write('entry not added to highscore, validation failed; '
            . 'POST parameters: ' . print_r($_POST, true));
    } else if (isSubmittedTooSoon(
            $current_time, $_SESSION['last_hs_submit_time'])) {
        log_write('entry not added to highscore, submitted too soon; '
            . 'time=' . $current_time . ', last_hs_submit_time='
            . $_SESSION['last_hs_submit_time'] . '; '
            . 'POST parameters: ' . print_r($_POST, true));
    } else if (!isHMACValid($h, $_SESSION['hs_h_data'])) {
        log_write('entry not added to highscore, wrong HMAC; '
            . 'hs_h_data=' . $_SESSION['hs_h_data'] . '; '
            . 'POST parameters: ' . print_r($_POST, true));
    } else if ($speed < $required_speed) {
        log_write('entry not added to highscore, test result not accepted; '
            . "speed=$speed, required_speed=$required_speed, "
            . "mistakes=$mistakes ; "
            . 'POST parameters: ' . print_r($_POST, true));
    } else if ($mistakes <= $MAX_MISTAKES) {
        // konwersja polskiego u�amka dziesi�tnego (przecinek)
        // na ameryka�ski (kropka)
        $speed = str_replace(',', '.', $speed);
        if (isset($_SERVER['HTTP_X_FORWARDED_FOR'])) {
            $ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
        } else {
            $ip = $_SERVER['REMOTE_ADDR'];
        }
        $username = pg_escape_string($username);
        $speed = pg_escape_string($speed);
        $mistakes = pg_escape_string($mistakes);
        $corrections = pg_escape_string($corrections);
        $pl = pg_escape_string($pl);
        $chars = pg_escape_string($chars);
        $minutes = pg_escape_string($minutes);
        $seconds = pg_escape_string($seconds);
        $query = "
            INSERT INTO tt.highscore
                (date_added, ip, username, speed, mistakes, corrections,
                    pl, chars, minutes, seconds)
                VALUES
                (NOW(), '$ip', '$username', $speed, $mistakes, $corrections,
                    '$pl', $chars, $minutes, $seconds)
        ";
        pg_query($query) or log_write("ERROR: problem with query: $query ("
            . pg_last_error() . ')');
    }

    unset($_SESSION['hs_h_data']);
    $_SESSION['last_hs_submit_time'] = time();
}

?>
