<?php
use yii\helpers\Html;
use yii\bootstrap\Nav;
use yii\bootstrap\NavBar;
use yii\widgets\Breadcrumbs;
use app\assets\AppAsset;
use yii\web\UrlManager;

/* @var $this \yii\web\View */
/* @var $content string */



AppAsset::register($this);
?>
<?php $this->beginPage() ?>
<!DOCTYPE html>
<html lang="<?= Yii::$app->language ?>">
<head>
    <meta charset="<?= Yii::$app->charset ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <?= Html::csrfMetaTags() ?>
    <title><?= Html::encode($this->title) ?></title>
    <?php $this->head() ?>
</head>
<body>

<?php $this->beginBody() ?>
    <div class="wrap">
        <?php
            NavBar::begin([
                'brandLabel' => 'Edukt',
                'brandUrl' => Yii::$app->homeUrl,
                'options' => [
                    'class' => 'navbar-inverse navbar-fixed-top',
                ],
            ]);
            echo Nav::widget([
                'options' => ['class' => 'navbar-nav navbar-right'],
            ]);



            $menuItems = [
              ['label' => 'Mensajes', 'url' => ['/notas/index']],
              ['label' => 'Documentos', 'url' => ['/docs/index']],
              ['label' => 'Administrar', 'items' => [
                  ['label' => 'Usuarios', 'url' => ['/users/index']],
                  '<li class="divider"></li>',
                  ['label' => 'Universidades', 'url' => ['/universidad/index']],
                ],
              ],

            ];

            $menuItemsLog = [
              ['label' => 'Login', 'url' => ['/user/login']],
            ];
            if (Yii::$app->user->isGuest) {

              echo Nav::widget([
                'options' => ['class' => 'navbar-nav navbar-right'],
                'items' => $menuItemsLog,
                ]);



            } else {
              $menuItems[] = [
                'label' => 'Logout (' . Yii::$app->user->identity->username . ')',
                'url' => ['/user/logout'],
                'linkOptions' => ['data-method' => 'post']
              ];

              echo Nav::widget([
                'options' => ['class' => 'navbar-nav navbar-right'],
                'items' => $menuItems,
                ]);

            }










            NavBar::end();
        ?>

        <div class="container">
            <?= Breadcrumbs::widget([
                'links' => isset($this->params['breadcrumbs']) ? $this->params['breadcrumbs'] : [],
            ]) ?>
            <?= $content ?>
        </div>
    </div>

    <footer class="footer">
        <div class="container">
            <p class="pull-left">&copy; i3soluciones.com <?= date('Y') ?></p>
            <p class="pull-right"><?= Yii::powered() ?></p>
        </div>
    </footer>

<?php $this->endBody() ?>
</body>
</html>
<?php $this->endPage() ?>
